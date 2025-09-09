package com.rollerspeed.rollerspeed.controller;

import com.rollerspeed.rollerspeed.model.GaleriaItem;
import com.rollerspeed.rollerspeed.service.GaleriaItemService;
import com.rollerspeed.rollerspeed.service.ClaseService;
import com.rollerspeed.rollerspeed.service.TestimonioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private TestimonioService testimonioService;

    @Autowired
    private ClaseService claseService;

    @Autowired
    private GaleriaItemService galeriaItemService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "¡Bienvenido a Roller Speed!");
        model.addAttribute("testimonios", testimonioService.listarTestimoniosActivos());
        model.addAttribute("clases", claseService.listarTodasLasClases());
        return "index"; // This will resolve to src/main/resources/templates/index.html
    }

    @GetMapping("/galeria")
    public String galeria(Model model) {
        List<GaleriaItem> items = galeriaItemService.findAll();
        model.addAttribute("items", items);
        return "galeria";
    }
}