package com.michaelcgood;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.michaelcgood.model.PagerModel;
import com.michaelcgood.model.SongModel;
import com.michaelcgood.uploader.FileUploadController;
import com.michaelcgood.uploader.StorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.michaelcgood.dao.MusicDAO;
import com.michaelcgood.dao.SongDAO;
import com.michaelcgood.model.MusicModel;

@Controller
public class WebController {

    private static final int BUTTONS_TO_SHOW = 3;
    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 5;
    private static final int[] PAGE_SIZES = { 5, 10, 50, 100 };

    @Autowired
    MusicDAO musicDAO;
    @Autowired
    SongDAO songDAO;
    @Autowired
    private final StorageService storageService;
    
    public WebController(StorageService storageService){
        this.storageService = storageService;
    }

    @GetMapping("/")
    public ModelAndView homepage(@RequestParam("pageSize") Optional<Integer> pageSize, @RequestParam("page") Optional<Integer> page) {

        ModelAndView modelAndView = new ModelAndView("uploadForm");
        //
        // Evaluate page size. If requested parameter is null, return initial
        // page size
        int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
        // Evaluate page. If requested parameter is null or less than 0 (to
        // prevent exception), return initial size. Otherwise, return value of
        // param. decreased by 1.
        int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;

        Page<MusicModel> Musiclist = musicDAO.findAll(new PageRequest(evalPage, evalPageSize));
        System.out.println("client list get total pages" + Musiclist.getTotalPages() + "client list get number " + Musiclist.getNumber());
        PagerModel pager = new PagerModel(Musiclist.getTotalPages(), Musiclist.getNumber(), BUTTONS_TO_SHOW);
 
        // add clientmodel
        modelAndView.addObject("Musiclist", Musiclist);
        // evaluate page size
        modelAndView.addObject("selectedPageSize", evalPageSize);
        // add page sizes
        modelAndView.addObject("pageSizes", PAGE_SIZES);
        // add pager
        modelAndView.addObject("pager", pager);
        modelAndView.addObject("files", storageService.loadAll()
            .map(path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class, "serveFile", path.getFileName()
                .toString())
                .build()
                .toString())
            .collect(Collectors.toList()));
        return modelAndView;

    }

    @RequestMapping(value = { "/view", "/view/", "/view/{pageSize}{page}/", "/view/{pageSize}", "/view/{pageSize}{page}/" }, method = RequestMethod.GET)
    public ModelAndView viewdoc( @RequestParam("pageSize") Optional<Integer> pageSize, @RequestParam("page") Optional<Integer> page, @RequestParam("rid") Optional<String> rid) {

        ModelAndView modelAndView = new ModelAndView();

        int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);

        int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;


        Page<SongModel> songList = songDAO.findAll(new PageRequest(evalPage, evalPageSize));
        System.out.println("PAGE RULE LIST :::::::: " + songList);

         PagerModel pager = new PagerModel(songList.getTotalPages(),songList.getNumber(),BUTTONS_TO_SHOW);

        modelAndView.addObject("ruleList", songList);
        modelAndView.addObject("selectedPageSize", evalPageSize);
        // add page sizes
        modelAndView.addObject("pageSizes", PAGE_SIZES);
        // add pager
        modelAndView.addObject("pager", pager);
        // modelAndView.addObject("rule", ruleText);
        modelAndView.setViewName("view");

        return modelAndView;

    }

}
