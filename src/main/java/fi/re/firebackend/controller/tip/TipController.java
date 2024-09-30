package fi.re.firebackend.controller.tip;

import fi.re.firebackend.dao.tip.TipDao;
import fi.re.firebackend.dto.tip.TipDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tip")
public class TipController {
    TipDao tipDao;

    public TipController(TipDao tipDao) {
        this.tipDao = tipDao;
    }

    @GetMapping("/findAll")
    public List<TipDto> findAll() {
        return tipDao.findAll();
    }
}

