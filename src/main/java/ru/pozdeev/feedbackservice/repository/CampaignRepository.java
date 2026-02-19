package ru.pozdeev.feedbackservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pozdeev.feedbackservice.model.Campaign;
import ru.pozdeev.feedbackservice.model.TriggerType;

import java.util.List;
import java.util.UUID;

public interface CampaignRepository extends JpaRepository<Campaign, UUID> {
    List<Campaign> findAllByTriggerTypeAndActiveIsTrue(TriggerType triggerType);
}
