package com.kassen.downloader.POJO;

import lombok.Data;

import java.util.List;

@Data
public class DownloadRequest {

    private String url;
    private String destination;
    private List<String> filesExisted;
    private Boolean overwrite;
}
