package pl.kuropatva.stocksim.controller;


import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kuropatva.stocksim.model.dto.web.LogListDto;
import pl.kuropatva.stocksim.service.LogService;

@RestController
@RequestMapping("/")
public class UtilController {

    private final LogService logService;
    private final ConfigurableApplicationContext context;

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
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (Exception _) {
            }
            System.exit(SpringApplication.exit(context, () -> 0));
        }).start();
    }
}
