package com.project.LabourLink.service;

import com.project.LabourLink.entity.Worker;
import com.project.LabourLink.repository.WorkerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkerService {

    private final WorkerRepository workerRepository;

    public WorkerService(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }

    public Worker saveWorker(Worker worker) {
        return workerRepository.save(worker);
    }

    public List<Worker> getAllWorkers() {
        return workerRepository.findAll();
    }
}