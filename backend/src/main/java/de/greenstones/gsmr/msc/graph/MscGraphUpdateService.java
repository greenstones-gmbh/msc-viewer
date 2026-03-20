package de.greenstones.gsmr.msc.graph;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.greenstones.gsmr.msc.MscViewerProperties;
import de.greenstones.gsmr.msc.MscViewerProperties.Msc;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MscGraphUpdateService {

    @Autowired
    MscViewerProperties props;

    @Autowired
    MscGraphService mscGraphService;

    @org.springframework.beans.factory.annotation.Value("${msc-viewer.graph.updateOnStartup:false}")
    boolean updateGraphOnStartup = false;

    @org.springframework.beans.factory.annotation.Value("${msc-viewer.graph.forceReloadOnStartup:false}")
    boolean forceReloadOnStartup = false;

    @org.springframework.beans.factory.annotation.Value("${msc-viewer.graph.graphUpdate.enabled:false}")
    boolean graphUpdateEnabled = false;

    @org.springframework.beans.factory.annotation.Value("${msc-viewer.graph.fullUpdate.enabled:false}")
    boolean fullUpdateEnabled = false;

    AtomicBoolean isRunning = new AtomicBoolean(false);

    protected void updateGraphs(boolean force) {

        if (isRunning.compareAndSet(false, true)) {
            for (Msc msc : props.getInstances()) {
                try {
                    log.info("updateGraph {} {}", msc.getId(), force);
                    mscGraphService.updateGraph(msc.getId(), force);
                } catch (Exception e) {
                    log.warn("can't update graph {}", msc.getId(), e);
                }
            }
            isRunning.set(false);
            log.info("Updates done");
        } else {
            log.info("updates running, skip for now ");
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void updateGraphsOnStartup() {
        if (updateGraphOnStartup) {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
            }
            updateGraphs(forceReloadOnStartup);
        }
    }

    @Scheduled(cron = "${msc-viewer.graph.graphUpdate.cron}")
    public void updateGraphsWithoutFetching() {

        if (graphUpdateEnabled) {
            log.info("updateGraphsWithoutFetching graphUpdateEnabled:{}", graphUpdateEnabled);
            updateGraphs(false);
        } else {
            log.info("skip updateGraphsWithoutFetching graphUpdateEnabled:{}", graphUpdateEnabled);
        }

    }

    @Scheduled(cron = "${msc-viewer.graph.fullUpdate.cron}")
    public void updateGraphs() {
        if (fullUpdateEnabled) {
            log.info("updateGraphs(full) fullUpdateEnabled:{}", fullUpdateEnabled);
            updateGraphs(true);
        } else {
            log.info("skip updateGraphs(full) fullUpdateEnabled:{}", fullUpdateEnabled);
        }

    }

}
