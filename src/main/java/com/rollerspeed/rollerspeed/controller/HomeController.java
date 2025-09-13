package com.rollerspeed.rollerspeed.controller;

import com.rollerspeed.rollerspeed.model.GaleriaItem;
import com.rollerspeed.rollerspeed.service.GaleriaItemService;
import com.rollerspeed.rollerspeed.service.ClaseService;
import com.rollerspeed.rollerspeed.service.TestimonioService;
import com.rollerspeed.rollerspeed.service.EventoService;
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

    @Autowired
    private EventoService eventoService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "¡Bienvenido a Roller Speed!");
        model.addAttribute("testimonios", testimonioService.listarTestimoniosActivos());
        model.addAttribute("clases", claseService.listarTodasLasClases());
        return "index";
    }

    @GetMapping("/eventos")
    public String eventos(Model model) {
        model.addAttribute("eventos", eventoService.listarEventosFuturos());
        return "eventos";
    }

    @GetMapping("/horario")
    public String horario(Model model) {
        model.addAttribute("clases", claseService.listarTodasLasClases());
        return "horario";
    }

    @GetMapping("/galeria")
    public String galeria(Model model) {
        List<GaleriaItem> items = galeriaItemService.findAll();
        model.addAttribute("items", items);
        return "galeria";
    }
}