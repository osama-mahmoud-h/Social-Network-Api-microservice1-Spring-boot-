package com.app.server.enums;

import lombok.Getter;

@Getter
public enum FileCategory {
    PROFILE_IMAGE("profiles/"),
    POST_ATTACHMENT("posts/");

    private final String folderPrefix;

    FileCategory(String folderPrefix) {
        this.folderPrefix = folderPrefix;
    }

}