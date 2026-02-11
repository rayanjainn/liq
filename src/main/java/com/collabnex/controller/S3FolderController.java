//package com.collabnex.controller;
//
//import com.example.location.service.S3FolderService;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/s3")
//@CrossOrigin(origins = "*")
//public class S3FolderController {
//
//    private final S3FolderService folderService;
//
//    public S3FolderController(S3FolderService folderService) {
//        this.folderService = folderService;
//    }
//
//    @PostMapping("/create-folder")
//    public String createFolder(
//            @RequestParam String parentFolder,
//            @RequestParam String subFolder
//    ) {
//        // Example: LayerIQ + xyz → LayerIQ/xyz/
//        String fullPath = parentFolder + "/" + subFolder;
//
//        folderService.createFolder(fullPath);
//        return "Folder created: " + fullPath;
//    }
//}






package com.collabnex.controller;

import com.collabnex.service.S3FolderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/s3")
@CrossOrigin(origins = "*")
public class S3FolderController {

    private final S3FolderService folderService;

    public S3FolderController(S3FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping("/create-folder")
    public String createFolder(
            @RequestParam String parentFolder,
            @RequestParam String subFolder
    ) {
        String fullPath = parentFolder + "/" + subFolder;
        folderService.createFolder(fullPath);
        return "Folder created: " + fullPath;
    }
}

