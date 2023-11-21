package com.kassen.filechecker.Controller;

import com.kassen.filechecker.POJO.CheckRequest;
import com.kassen.filechecker.POJO.CheckResult;
import com.kassen.filechecker.Service.CheckService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class CheckController {

    private final CheckService checker;

    public CheckController(CheckService checker) {
        this.checker = checker;
    }

    @PostMapping("/check")
    public List<CheckResult> checkFiles(@RequestBody CheckRequest checkRequest) {

//        System.out.println(checkRequest.getCsvData());
//        System.out.println("controller: " + checkRequest.getDirectories());

        List<CheckResult> results = null;

        try {
            results = checker.checkFiles(checkRequest);
        } catch(Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        return results;
    }
}
