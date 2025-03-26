package de.greenstones.gsmr.msc.core;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.greenstones.gsmr.msc.ApplicationException;
import de.greenstones.gsmr.msc.MscViewerProperties;
import de.greenstones.gsmr.msc.MscViewerProperties.Msc;
import de.greenstones.gsmr.msc.types.ConfigType;
import de.greenstones.gsmr.msc.types.FrontendConfigurationBuilder;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/msc")
@CrossOrigin
@Slf4j
public class MscController {

	@Autowired
	MscResolver mscs;

	@Autowired
	MscViewerProperties props;

	@GetMapping()
	public List<Msc> instances() {
		return props.getInstances();
	}

	@GetMapping("/{mscId}")
	public Object meta(@PathVariable String mscId) {
		log.debug("meta {}", mscId);
		Map<String, ConfigType> types = mscs.find(mscId).getTypes();
		return new FrontendConfigurationBuilder(types).build();
	}

	@GetMapping("/{mscId}/{type}")
	public Object list(@PathVariable String mscId, @PathVariable String type,
			@RequestParam(required = false, defaultValue = "false") boolean force) {

		log.debug("list {} {}", mscId, type);

		return mscs.find(mscId).execute(repository -> repository.findAll(type, force));
	}

	@GetMapping("/{mscId}/{type}/{id}")
	public Object detail(@PathVariable String mscId, @PathVariable String type, @PathVariable String id,
			@RequestParam(required = false, defaultValue = "false") boolean force) {
		log.debug("details {} {} {}", mscId, type, id);
		return mscs.find(mscId).execute(repository -> repository.findOne(type, id, force));

	}

	@ExceptionHandler({ ApplicationException.class })
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public String handleError(Exception e) {
		return e.getMessage();
	}

}
