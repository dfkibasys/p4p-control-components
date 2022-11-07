package de.dfki.cos.basys.p4p.controlcomponent.mir.service;

import de.dfki.cos.basys.common.rest.mir.dto.MissionInstanceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class MissionScheduler {
    /**
     * Logger
     */
    private static final Logger LOG =  LoggerFactory.getLogger(MirServiceROSImpl.class.getName());
    /**
     * Defines the maximum number of missions this scheduler can handle at once
     */
    private static final int MAX_NUM_MSSIONS = 20;

    /**
     * Priority queue as main data structure for ordered execution of missions
     * regarding their internal priority and age
     */
    private final BlockingQueue<MissionInstanceInfo> _queue =
            new PriorityBlockingQueue<>(MAX_NUM_MSSIONS,
                    (MissionInstanceInfo s, MissionInstanceInfo t) -> {
                        if(s.priority == t.priority)
                            return s.ordered.compareTo(t.ordered);
                        else
                            return Integer.compare(s.priority, t.priority);
                    });

    /**
     * Lock for ensuring thread safe concurrent modifications/ops
     */
    private final Object _lock = new Object();

    /**
     * Running flag for scheduler
     */
    private volatile boolean _running = true;

    private MissionHandler _handler;

    public MissionScheduler(MissionHandler handler) {
        this._handler = handler;
    }

    public void start() throws InterruptedException {
        LOG.info("MISSION SCHEDULER STARTED");
        _running = true;
        while (_running) {
            //LOG.info("TAKE");
            MissionInstanceInfo mii = _queue.take();
            if (mii != null) {
                _handler.handleMission(mii);
            }
            waitForNextMission();
        }
    }

    public void stop() {
        _running = false;
    }

    private void waitForNextMission() throws InterruptedException {
        LOG.info("waitForNextMission");
        synchronized (_lock) {
            MissionInstanceInfo nextMission = _queue.peek();
            while (nextMission == null || _handler.isBusy()) {
                //LOG.info("LOOP");
                if (nextMission == null || _handler.isBusy())
                    _lock.wait();
                nextMission = _queue.peek();
            }
        }
    }

    public boolean add(MissionInstanceInfo mii) {
        boolean ret = false;
        synchronized (_lock) {
            if(_queue.size()<MAX_NUM_MSSIONS)
                ret = _queue.offer(mii);
            _lock.notify();
        }
        if(ret)
            LOG.info("ADDED MISSION {}",mii.toString());
        else
            LOG.info("DID NOT ADD MISSION {}",mii.toString());
        return ret;
    }

    public boolean remove(MissionInstanceInfo mii) {
        boolean ret;
        synchronized (_lock) {
            ret = _queue.remove(mii);
            _lock.notify();
        }
        if(ret)
            LOG.info("REMOVED MISSION {}",mii.toString());
        else
            LOG.info("DID NOT REMOVE MISSION {}",mii.toString());
        return ret;
    }

    public void clear() {
        synchronized (_lock) {
            _queue.clear();
            _lock.notify();
        }
    }

}
