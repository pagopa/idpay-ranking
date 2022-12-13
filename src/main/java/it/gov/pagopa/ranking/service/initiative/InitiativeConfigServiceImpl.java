package it.gov.pagopa.ranking.service.initiative;

import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.repository.InitiativeConfigRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class InitiativeConfigServiceImpl implements InitiativeConfigService{
    private final InitiativeConfigRepository initiativeConfigRepository;

    public InitiativeConfigServiceImpl(InitiativeConfigRepository initiativeConfigRepository) {
        this.initiativeConfigRepository = initiativeConfigRepository;
    }

    @Override
    public InitiativeConfig save(InitiativeConfig initiativeConfig) {
        return initiativeConfigRepository.save(initiativeConfig);
    }

    @Override
    public InitiativeConfig findById(String initiativeId) {
        return initiativeConfigRepository.findById(initiativeId).orElse(null);
    }

    @Override
    public Optional<InitiativeConfig> findByIdOptional(String initiativeId) {
        return initiativeConfigRepository.findById(initiativeId);
    }
}
