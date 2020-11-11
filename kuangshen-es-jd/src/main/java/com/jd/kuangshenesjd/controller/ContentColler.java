package com.jd.kuangshenesjd.controller;

import com.jd.kuangshenesjd.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

//请求编写
@RestController
public class ContentColler {
    @Autowired
    private ContentService contentService;
    @GetMapping("/parse/{keyword}")
    public Boolean parse(@PathVariable("keyword") String keword) throws IOException {
        return contentService.parseContent(keword);
    }
    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> search(@PathVariable("keyword") String keyword,
                                          @PathVariable("pageNo") int pageNo,
                                          @PathVariable("pageSize") int pageSize) throws IOException {
        //普通查询
        //return contentService.searchPage(keyword, pageNo, pageSize);
        //高亮查询
        return contentService.searchHightPage(keyword, pageNo, pageSize);
    }
}
