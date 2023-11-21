package com.kassen.hardlink;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.kassen.hardlink.Mapper")
public class HardlinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(HardlinkApplication.class, args);
    }

}
