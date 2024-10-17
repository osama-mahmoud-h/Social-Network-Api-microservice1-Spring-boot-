package com.example.server.service.impl;

import com.example.server.dto.request.post.CreatePostRequestDto;
import com.example.server.mapper.FileMapper;
import com.example.server.mapper.PostMapper;
import com.example.server.model.AppUser;
import com.example.server.model.Comment;
import com.example.server.model.File;
import com.example.server.model.Post;
import com.example.server.dto.response.CommentsResponseDto;
import com.example.server.dto.response.PostResponseDto;
import com.example.server.dto.response.AppUserResponseDto;
//import com.example.server.repository.PostLikeRepository;
import com.example.server.repository.FileRepository;
import com.example.server.repository.PostRepository;
import com.example.server.repository.AppUserRepository;
import com.example.server.service.PostService;
import com.example.server.service.UserService;
import com.example.server.utils.fileStorage.FilesStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImp implements PostService {
    private final PostRepository postRepository;
    private final FilesStorageService filesStorageService;
    private final AppUserRepository appUserRepository;
    private final UserService userService;
    private final PostMapper postMapper;
    private final FileMapper fileMapper;
    private final FileRepository fileRepository;
   // private final PostLikeRepository likeRepository;

    public Post getPostById(Long postId){
//        Optional<Post> post = postRepository.findById(postId);
//        if(post.isEmpty()){
//            throw new CustomErrorException("post not found");
//        }
//        return post.get();
        return  null;
    }

    @Override
    @Transactional
    public Post savePost(AppUser currentUser, CreatePostRequestDto createPostRequestDto){
        Post newPost = this.postMapper.mapCreatePostRequestDtoToPost(createPostRequestDto);
        Set<File> uploadedFiles = this.uploadFiles(createPostRequestDto.getFiles());

        List<File> savedFiles = this.fileRepository.saveAll(uploadedFiles);

        newPost.setFiles(new HashSet<>(savedFiles));
        newPost.setAuthor(currentUser);

        return this.postRepository.save(newPost);
    }

    @Override
    @Transactional
    public ResponseEntity<Object> likePost(HttpServletRequest request, Long postId, byte like_type){

//        if(like_type<=0||like_type>7){
//            throw new CustomErrorException("Like Error (out of range)");
//        }
//        Optional<User> currUser  = authenticatedUser.getCurrentUser(request);
//
//        Post saved_post = this.getPostById(postId);
//
//        //  System.out.println("post saved--------------------- "+saved_post.toString());
//
//        String liked="liked";
//        //  removeLikeOnPost(currUser.get().getId(), postId);
//
//        PostLike like = ifUserLikedPost(currUser.get().getId(), saved_post);
//        if (like!=null){
//            if(like.getType()==like_type){ // already like using same reaction ,remove it
//                removeLikeOnPost(currUser.get().getId(), postId);
//                liked = "unliked";
//            }else{ //update like
//                like.setType(like_type);
//                likeRepository.save(like);
//                liked="updated";
//            }
//        }else {
//            PostLike newLike = new PostLike();
//            newLike.setLiker(currUser.get());
//            newLike.setPost(saved_post);
//            newLike.setType(like_type);
//
//            likeRepository.save(newLike);
//
//            liked = "liked";
//        }
//
//        return ResponseHandler.generateResponse("post "+liked+" ok",
//                HttpStatus.CREATED, saved_post);
        return null;
    }

//    @Override
//    //public PostLike ifUserLikedPost(Long userId, Post saved_post){
//
////        PostLike like =  saved_post.getLikedPosts()
////                .stream()
////                .filter(lik ->lik.getLiker().getId().equals(userId))
////                .findAny().orElse(null);
////        return  like;
//        return null;
//    }


//    @Override
//    public PostLike ifILikedThisPost(HttpServletRequest req, Long postId){
////        Optional<User> me = authenticatedUser.getCurrentUser(req);
////        Post saved_post = getPostById(postId);
////        PostLike like = ifUserLikedPost(me.get().getId(), saved_post);
////        if(like!=null){
////            return like;
////        }
////        return new PostLike();
//        return null;
//    }



    @Override
    public List<PostResponseDto> getAllPosts(HttpServletRequest req) {
//        List<Post> posts = postRepository.findAll();
//        List<PostResponceDto> allposts = new ArrayList<PostResponceDto>();
//       // Optional<User> me = authenticatedUser.getCurrentUser(req);
//
//        for (Post post : posts){
//           // System.out.println("author "+post.getAuthor());
//            PostResponceDto postDto = mapPostToPostResponce(post);
//            //if i like this post
//            if(req==null ) { // for not athuenticated users
//
//                PostLike like = ifILikedThisPost(req, post.getId());
//                postDto.setMyFeed(like.getType());
//
//                Map<Byte, Long> likeTypeCount = new HashMap<>();
//                for (PostLike like_ : post.getLikedPosts()) {
//                    likeTypeCount.put(like_.getType(),
//                            likeTypeCount.getOrDefault(like_.getType(), 0L) + 1L);
//                }
//                postDto.setFeeds(likeTypeCount);
//            }
//
//            allposts.add(postDto);
//        }
//        return allposts;
        return null;
    }

    @Override
    public PostResponseDto getPostDetails(Long postId){
//        Post post  = getPostById(postId);
//        PostResponceDto postDto = mapPostToPostResponce(post);
//
//        Map<Byte,Long>typeCount = new HashMap<>();
//        for(PostLike like :post.getLikedPosts()){
//            typeCount.put(like.getType(),
//                    typeCount.getOrDefault(like.getType(),0L) + 1L);
//        }
//        postDto.setFeeds(typeCount);
//        return postDto;
        return null;
    }

    @Override
    public List<CommentsResponseDto> getAllCommentsOnPost(Long post_id) {
//        Optional<Post> post = postRepository.findById(post_id);
//        if(post.isEmpty()){
//            throw new CustomErrorException(HttpStatus.NOT_FOUND, "post "+post_id+" not found");
//        }
//        Set<Comment> comments = post.get().getComments();
//
//        List<CommentsResponseDto> allcomments = new ArrayList<>();
//
//        for (Comment comment:comments) {
//            CommentsResponseDto commentDto = mapCommentToCommentResponce(comment);
//            allcomments.add(commentDto);
//        }
//        return allcomments;
        return null;
    }


    @Override
    public Post deletePost(HttpServletRequest servletRequest,Long post_id){
//        Optional<User> author =  authenticatedUser.getCurrentUser(servletRequest);
//        Post post = getPostById(post_id);
//        if(post.getAuthor().getId().equals(author.get().getId())){
//            postRepository.deleteById(post.getId());
//            return post;
//        }
//        throw new CustomErrorException(HttpStatus.FORBIDDEN,"you arent athor of this post");
        return null;
    }

    @Override
    public Post updatePost(HttpServletRequest servletRequest, Long post_id,String text) {
//        Optional<User> author = authenticatedUser.getCurrentUser(servletRequest);
//        Post post = getPostById(post_id);
//        if(!post.getAuthor().getId().equals(author.get().getId())){
//            throw new CustomErrorException(HttpStatus.FORBIDDEN," you arent '[author]' of this post");
//        }
//        post.setText(text);
//        postRepository.save(post);
//        return post;
        return null;
    }


    private PostResponseDto mapPostToPostResponce(Post post){
//        //map post to postDto
//        PostResponceDto  postResponceDto = new PostResponceDto();
//        postResponceDto.setId(post.getId());
//        postResponceDto.setText(post.getText());
//        postResponceDto.setImages_url(post.getImages_url());
//        postResponceDto.setVedio_url(post.getVedio_url());
//        postResponceDto.setFile_url(post.getFile_url());
//        //postResponceDto.setLikes(post.getLikesCount());
//        //create author dto
//        AppUserResponseDto authorDto = mapUserToUserResponce(post.getAuthor());
//
//        //set Author
//        postResponceDto.setAuthor(authorDto);
//
//        return postResponceDto;
        return null;
    }
    private CommentsResponseDto mapCommentToCommentResponce(Comment comment){
//        //map post to postDto
//        CommentsResponseDto commentDto = new CommentsResponseDto();
//        commentDto.setId(comment.getId());
//        commentDto.setText(comment.getText());
//
//        //create author dto
//        AppUserResponseDto authorDto = mapUserToUserResponce(comment.getAuthor());
//
//        //set Author
//        commentDto.setAuthor(authorDto);
//
//        return commentDto;
        return null;
    }

    private AppUserResponseDto mapUserToUserResponce(AppUser user){
//        //create author dto
//        AppUserResponseDto authorDto = new AppUserResponseDto();
//        authorDto.setId(user.getId());
//        authorDto.setUsername(user.getUsername());
//        authorDto.setEmail(user.getEmail());
//        authorDto.setImage_url(user.getProfile().getImage_url());
//
//        return authorDto;
        return null;
    }

    private Set<File> uploadFiles(MultipartFile[] multipartFiles){
        return Arrays.stream(multipartFiles).map(this.fileMapper::mapMultiPartFileToFileSchema)
                .collect(Collectors.toSet());
    }
}
