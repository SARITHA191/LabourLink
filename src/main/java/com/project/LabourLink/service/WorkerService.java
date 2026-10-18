
package com.project.LabourLink.service;

import com.project.LabourLink.entity.Worker;
import com.project.LabourLink.repository.WorkerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

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

    // Find available workers suitable for a job
    public List<Worker> findMatchingWorkers(
            String workType,
            String jobLocation) {

        if (workType == null || jobLocation == null) {
            return List.of();
        }

        String requiredSkill = workType.trim()
                .toLowerCase(Locale.ROOT);

        String location = jobLocation.trim();

        return workerRepository.findAll()
                .stream()
                .filter(Worker::isAvailable)
                .filter(worker -> worker.getSkills() != null
                        && worker.getLocation() != null)
                .filter(worker -> worker.getLocation().trim()
                        .equalsIgnoreCase(location))
                .filter(worker -> worker.getSkills().toLowerCase(Locale.ROOT)
                        .contains(requiredSkill))
                .toList();
    }

    // Get a worker by ID
    public Worker getWorkerById(Long id) {
        return workerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Worker not found with ID: " + id));
    }
}
