package com.example.server.services.impl;

import com.example.server.exceptions.CustomErrorException;
import com.example.server.models.*;
import com.example.server.payload.response.CommentsResponseDto;
import com.example.server.payload.response.PostResponceDto;
import com.example.server.payload.response.UserResponceDto;
import com.example.server.repository.PostLikeRepository;
import com.example.server.repository.PostRepository;
import com.example.server.repository.UserRepository;
import com.example.server.security.jwt.AuthenticatedUser;
import com.example.server.services.FilesStorageService;
import com.example.server.services.PostService;
import com.example.server.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImp implements PostService {
    private final AuthenticatedUser authenticatedUser;
    private final PostRepository postRepository;
    private final FilesStorageService filesStorageService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final KafkaServiceImp kafkaServiceImp;
    private final PostLikeRepository likeRepository;

    public Post getPostById(Long postId){
        Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty()){
            throw new CustomErrorException("post not found");
        }
        return post.get();
    }

    @Override
    public PostResponceDto savePost(HttpServletRequest request,
                                    MultipartFile[] images,
                                    MultipartFile video,
                                    MultipartFile file,
                                    String text
    ){
        //System.out.println("================================================================: "
        //         +images.length+" image[0]: "+images[0].getOriginalFilename());
        String video_url = "uploads/";
        String file_url = "uploads/";
        String [] image_urls = new String[images!=null ? images.length : 0];

            String randomString = String.valueOf(new Random().nextLong());
            if(video!=null && !video.isEmpty()){
                if(!video.getContentType().startsWith("video")){
                    throw new CustomErrorException("not valid video");
                }
                video_url +=randomString+"_"+video.getOriginalFilename();
                //upload video to server
                filesStorageService.save(video,randomString+"_"+video.getOriginalFilename());
            }

            if(images!=null && images.length>0){
                for (int i = 0; i < Math.min(images.length,10); i++) { // max 10 elements

                    MultipartFile image = images[i];
                    if(image==null || image.isEmpty()){
                        continue;
                        //throw new CustomErrorException("not valid image");
                    }
                    if(!image.getContentType().startsWith("image")){
                        throw new CustomErrorException("not valid image");
                    }
                    String extension = "";
                    if (image != null && image.getOriginalFilename().lastIndexOf(".") > 0) {
                        extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".") + 1);
                        System.out.print("extension: " + extension+"  , ");
                        System.out.println("extension: " + image.getContentType());

                    }
                    image_urls[i] = "uploads/"+randomString+"_"+image.getOriginalFilename();
                    //upload image to server
                    filesStorageService.save(image,randomString+"_"+image.getOriginalFilename());

                }
            }

            if(file!=null && !file.isEmpty()){
                System.out.println("file type: " + file.getContentType());
                if(!file.getContentType().startsWith("application")){
                    throw new CustomErrorException("not valid file");
                }
                file_url +=randomString+"_"+file.getOriginalFilename();
                //upload image to server
                filesStorageService.save(file,randomString+"_"+file.getOriginalFilename());
            }

            if((text==null || text.trim().length()==0)
                    && (file==null||file.isEmpty())
                    && (images==null||images.length==0)
                    && (video==null|| video.isEmpty())
            ){
                throw new CustomErrorException(HttpStatus.NOT_FOUND,"post is empty");
            }
            //getUser
            Optional<User> currUser = authenticatedUser.getCurrentUser(request);

            //create new post
            Post newPost = new Post();
            newPost.setLikes(0l);
            newPost.setText(text==null ? "" : text);
            newPost.setAuthor(currUser.get());
            newPost.setVedio_url(video!=null ? video_url : null);
            newPost.setImages_url(image_urls);
            newPost.setFile_url(file!=null ? file_url : null);

            System.out.println("herrrrrrrrrrrrrrrrrrrrrrrrrrre"+newPost);
            newPost = postRepository.save(newPost);

            // userRepository.save(currUser.get());

        System.out.println("post id: "+newPost);
            //publish post as message for kafka
            PostResponceDto postResponceDto = mapPostToPostResponce(newPost);
            //  kafkaServiceImp.publishMessage(postResponceDto);

            return postResponceDto;

    }

    @Override
    @Transactional
    public String likePost(HttpServletRequest request, Long postId, byte like_type){

        if(like_type<=0||like_type>7){
            throw new CustomErrorException("Like Error (out of range)");
        }
        Optional<User> currUser  = authenticatedUser.getCurrentUser(request);

        Post saved_post = this.getPostById(postId);

        //  System.out.println("post saved--------------------- "+saved_post.toString());

        String liked="liked";
        //  removeLikeOnPost(currUser.get().getId(), postId);

        PostLike like = ifUserLikedPost(currUser.get().getId(), saved_post);

        System.out.println("likeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee: "+like);

        if (like!=null){
            if(like.getType()==like_type){ // already like using same reaction ,remove it
                removeLikeOnPost(currUser.get().getId(), postId);
                System.out.println("userrrrrrrrrrrrrrrrrrr like "+like);
                return "0";
            }else{ //update like
                like.setType(like_type);
                likeRepository.save(like);
               // return  String.valueOf(like_type); //will return automatically
            }
        }else {
            PostLike newLike = new PostLike();
            newLike.setLiker(currUser.get());
            newLike.setPost(saved_post);
            newLike.setType(like_type);

            likeRepository.save(newLike);

            liked = "liked";
        }

        return String.valueOf(like_type);
    }

    @Override
    public PostLike ifUserLikedPost(Long userId, Post saved_post){

        PostLike like =  saved_post.getLikedPosts()
                .stream()
                .filter(lik ->lik.getLiker().getId().equals(userId))
                .findAny().orElse(null);
        return  like;
    }


    @Override
    public PostLike ifILikedThisPost(HttpServletRequest req, Long postId){
        Optional<User> me = authenticatedUser.getCurrentUser(req);
        Post saved_post = getPostById(postId);
        PostLike like = ifUserLikedPost(me.get().getId(), saved_post);
        if(like!=null){
            return like;
        }
        return new PostLike();
    }

    //  @Transactional(propagation = Propagation.REQUIRED)
    private void removeLikeOnPost(Long user_id, Long post_id){
        likeRepository.deleteLikeOnPost(user_id,post_id);
    }


    @Override
    public List<PostResponceDto> getAllPosts(HttpServletRequest req) {
        List<Post> posts = postRepository.findAll();
        List<PostResponceDto> allposts = new ArrayList<PostResponceDto>();
       // Optional<User> me = authenticatedUser.getCurrentUser(req);

        for (Post post : posts){
           // System.out.println("author "+post.getAuthor());
            PostResponceDto postDto = mapPostToPostResponce(post);
            //if i like this post
            if(req!=null && req.getHeader("Authorization")!=null ) { // for not athuenticated users
                PostLike like = ifILikedThisPost(req, post.getId());
                postDto.setMyFeed(like.getType());
            }
            Map<Byte, Long> likeTypeCount = new HashMap<>();
            for (PostLike like_ : post.getLikedPosts()) {
                likeTypeCount.put(like_.getType(),
                        likeTypeCount.getOrDefault(like_.getType(), 0L) + 1L);
            }
            postDto.setFeeds(likeTypeCount);

            allposts.add(postDto);
        }
        return allposts;
    }

    @Override
    public PostResponceDto getPostDetails(Long postId){
        Post post  = getPostById(postId);
        PostResponceDto postDto = mapPostToPostResponce(post);

        Map<Byte,Long>typeCount = new HashMap<>();
        for(PostLike like :post.getLikedPosts()){
            typeCount.put(like.getType(),
                    typeCount.getOrDefault(like.getType(),0L) + 1L);
        }
        postDto.setFeeds(typeCount);
        return postDto;
    }




    @Override
    public Post deletePost(HttpServletRequest servletRequest,Long post_id){
        Optional<User> author =  authenticatedUser.getCurrentUser(servletRequest);
        Post post = getPostById(post_id);
        if(post.getAuthor().getId().equals(author.get().getId())){
            postRepository.deleteById(post.getId());
            return post;
        }
        throw new CustomErrorException(HttpStatus.FORBIDDEN,"you arent athor of this post");
    }

    @Override
    public Post updatePost(HttpServletRequest servletRequest, Long post_id,String text) {
        Optional<User> author = authenticatedUser.getCurrentUser(servletRequest);
        Post post = getPostById(post_id);
        if(!post.getAuthor().getId().equals(author.get().getId())){
            throw new CustomErrorException(HttpStatus.FORBIDDEN," you arent '[author]' of this post");
        }
        post.setText(text);
        postRepository.save(post);
        return post;
    }


    private PostResponceDto mapPostToPostResponce(Post post){
        //map post to postDto
        PostResponceDto  postResponceDto = new PostResponceDto();
        postResponceDto.setId(post.getId());
        postResponceDto.setText(post.getText());
        postResponceDto.setImages_url(post.getImages_url());
        postResponceDto.setVedio_url(post.getVedio_url());
        postResponceDto.setFile_url(post.getFile_url());

        if(post.getComments()!=null)
        postResponceDto.setComments_count((long) post.getComments().size());

        //postResponceDto.setLikes(post.getLikesCount());
        //create author dto
        UserResponceDto authorDto = mapUserToUserResponce(post.getAuthor());

        //set Author
        postResponceDto.setAuthor(authorDto);

        return postResponceDto;
    }
    private CommentsResponseDto mapCommentToCommentResponce(Comment comment){
        //map post to postDto
        CommentsResponseDto commentDto = new CommentsResponseDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());

        //create author dto
        UserResponceDto authorDto = mapUserToUserResponce(comment.getAuthor());

        //set Author
        commentDto.setAuthor(authorDto);

        return commentDto;
    }

    private UserResponceDto mapUserToUserResponce(User user){
        //create author dto
        UserResponceDto authorDto = new UserResponceDto();
        authorDto.setId(user.getId());
        authorDto.setUsername(user.getUsername());
        authorDto.setEmail(user.getEmail());
        authorDto.setImage_url(user.getProfile().getImage_url());

        return authorDto;
    }
}
