package pl.kuropatva.stocksim.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.*;
import pl.kuropatva.stocksim.model.dto.web.LogListDto;
import pl.kuropatva.stocksim.service.LogService;

@RestController
@RequestMapping("/")
public class UtilController {

    private LogService logService;
    private ConfigurableApplicationContext context;

    public UtilController(LogService logService, ConfigurableApplicationContext context) {
        this.logService = logService;
        this.context = context;
    }

    @GetMapping("/log")
    public LogListDto getLog() {
        return logService.getLogListDto();
    }

    @PostMapping("/chaos")
    public void killInstance() {
        new Thread(() -> { try { Thread.sleep(1000); } catch (Exception _) {} System.exit(SpringApplication.exit(context, () -> 0)); }).start();
    }
}
