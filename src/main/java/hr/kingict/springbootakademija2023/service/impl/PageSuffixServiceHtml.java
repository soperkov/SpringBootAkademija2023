package hr.kingict.springbootakademija2023.service.impl;

import hr.kingict.springbootakademija2023.service.PageSuffixService;
import jdk.jfr.Name;
import org.springframework.stereotype.Service;

@Service("html")
public class PageSuffixServiceHtml implements PageSuffixService {

    @Override
    public String getSuffix(){
        return "html";
    }

}
