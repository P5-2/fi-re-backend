package fi.re.firebackend.controller.forex;

import fi.re.firebackend.service.forex.ForexService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/forex")
public class ForexController {


    private final ForexService forexService;

    public ForexController(ForexService forexService) {
        this.forexService = forexService;
    }

    @GetMapping("/forexInfo")
    public List<ForexInfo> forexInfo() {
        
    }
}
