//package com.example.server.controller;
//
//import com.example.server.dto.response.ResponseHandler;
//import com.example.server.utils.fileStorage.FilesStorageService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.core.io.Resource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/file")
//public class FilesController {
//    private final FilesStorageService filesStorageService;
//
//    @PostMapping("/upload")
//    public ResponseEntity<Object> uploadFile(@RequestParam("file") MultipartFile[] files,
//                                             @RequestParam("data") String data) {
//
//     //   System.out.println("data: "+data);
//        int i = 0;
//        String[] messages = new String[files.length];
//        MultipartFile currFile = null;
//        String message = "";
//        try {
//            for (MultipartFile file:files) {
//                currFile = file;
//                //System.out.println("file :"+file.getOriginalFilename());
//
//              //  filesStorageService.save(file);
//
//                message = "Uploaded the file successfully: " + file.getOriginalFilename();
//                messages[i++] = message;
//            }
//
//
//            return ResponseHandler.generateResponse("all files uploded successfully",HttpStatus.OK,messages);
//        } catch (Exception e) {
//            message = "Could not upload the file: " + currFile.getOriginalFilename() + ". Error: " + e.getMessage();
//            return ResponseHandler.generateResponse(message,HttpStatus.EXPECTATION_FAILED,"");
//        }
//    }
//
//    @GetMapping("/all")
//    public ResponseEntity<Object> getListFiles() {
////        List<FileInfo> fileInfos = filesStorageService.loadAll().map(path -> {
////            String filename = path.getFileName().toString();
////            String url = MvcUriComponentsBuilder
////                    .fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();
////
////            return new FileInfo(filename, url);
////        }).collect(Collectors.toList());
//       /// return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
//        return ResponseHandler.generateResponse("files get success",HttpStatus.OK,null);
//    }
//
//    @GetMapping("/{filename:.+}")
//    @ResponseBody
//    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
//        Resource file = filesStorageService.load(filename);
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
//    }
//
//}
