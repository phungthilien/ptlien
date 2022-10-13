package com.techgeeknext.controller;

import java.util.List;

import com.techgeeknext.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techgeeknext.entity.Menu;
import com.techgeeknext.repository.MenuRepo;

@RestController
@RequestMapping("/menu")
public class MenuController {
	
    @Autowired
    private MenuRepo menuRepo;
    @Autowired
    private MenuRepository menuRepository;

    @PostMapping
    public Menu save(@RequestBody Menu menuDetails) {
        return menuRepo.save(menuDetails);
    }

    @GetMapping
    public List<Menu> getMenus() {
        return menuRepo.findAll();
    }

    @GetMapping("/{id}")
    public Menu findMenuItemById(@PathVariable int id) {
        return menuRepo.findItemById(id);
    }


    @DeleteMapping("/{id}")
    public String deleteMenuById(@PathVariable int id)   {
    	return menuRepo.deleteMenu(id);
	}

    @PostMapping("/user")
    public Menu save1(@RequestBody Menu menuDetails){
        return menuRepository.save(menuDetails);
    }
    @GetMapping("/user")
    public Iterable<Menu> getAll() {
        return menuRepository.findAll();
    }

}
