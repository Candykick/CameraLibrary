package com.candykick.cdkgallery.data;

/**
 * Created by candykick on 2019. 9. 23..
 */

public class GalleryFolderData {
    public String folderName, folderImageSrc, folderId;
    public int folderImageNum;

    public GalleryFolderData() {}

    public GalleryFolderData(String folderName, int folderImageNum, String folderImageSrc, String folderId) {
        this.folderName = folderName;
        this.folderImageNum = folderImageNum;
        this.folderImageSrc = folderImageSrc;
        this.folderId = folderId;
    }
}