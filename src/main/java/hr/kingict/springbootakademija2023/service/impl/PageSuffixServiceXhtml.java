package hr.kingict.springbootakademija2023.service.impl;

import hr.kingict.springbootakademija2023.service.PageSuffixService;
import org.springframework.stereotype.Service;

@Service("xhtml")
public class PageSuffixServiceXhtml implements PageSuffixService {

    @Override
    public String getSuffix(){
        return "xhtml";
    }

}
