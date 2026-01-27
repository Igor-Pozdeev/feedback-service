package ru.pozdeev.feedbackservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pozdeev.feedbackservice.model.Campaign;

import java.util.UUID;

public interface CampaignRepository extends JpaRepository<Campaign, UUID> {
}
