package com.kassen.filechecker.POJO;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CheckRequest {

    private Map<String, Integer> csvData;
    private List<String> directories;
}
