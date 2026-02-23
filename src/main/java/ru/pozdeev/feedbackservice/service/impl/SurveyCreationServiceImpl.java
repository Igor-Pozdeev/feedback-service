package ru.pozdeev.feedbackservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pozdeev.feedbackservice.dto.event.CustomerEvent;
import ru.pozdeev.feedbackservice.exception.InvalidCustomerEventException;
import ru.pozdeev.feedbackservice.mapper.SurveyMapper;
import ru.pozdeev.feedbackservice.model.Campaign;
import ru.pozdeev.feedbackservice.model.Survey;
import ru.pozdeev.feedbackservice.model.TriggerType;
import ru.pozdeev.feedbackservice.repository.CampaignRepository;
import ru.pozdeev.feedbackservice.repository.SurveyRepository;
import ru.pozdeev.feedbackservice.service.SurveyCreationService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyCreationServiceImpl implements SurveyCreationService {

    private final CampaignRepository campaignRepository;
    private final SurveyRepository surveyRepository;
    private final SurveyMapper surveyMapper;

    @Override
    @Transactional
    public void processCustomerEvent(CustomerEvent event) {
        validateEvent(event);

        TriggerType triggerType = event.eventType();
        List<Campaign> activeCampaigns = campaignRepository.findAllByTriggerTypeAndActiveIsTrue(triggerType);
        if (activeCampaigns.isEmpty()) {
            log.warn("Нет ни одной активной кампании");
            return;
        }

        activeCampaigns.stream()
                .filter(campaign -> !surveyRepository.existsByContextIdAndCampaignId(event.contextId(), campaign.getId()))
                .forEach(campaign -> saveSurvey(event, campaign));
    }

    private void validateEvent(CustomerEvent event) {
        if (TriggerType.TICKET_CLOSED.equals(event.eventType())) {
            if (event.metadata() == null || !event.metadata().containsKey("resolution_time_hours")) {
                throw new InvalidCustomerEventException(
                        "Для события TICKET_CLOSED поле metadata.resolution_time_hours является обязательным"
                );
            }
        }
        if (TriggerType.ORDER_COMPLETED.equals(event.eventType())) {
            if (event.metadata() == null || !event.metadata().containsKey("order_total")) {
                throw new InvalidCustomerEventException(
                        "Для события ORDER_COMPLETED поле metadata.order_total является обязательным"
                );
            }
        }
    }

    private void saveSurvey(CustomerEvent event, Campaign campaign) {
        Survey survey = surveyMapper.toSurvey(event, campaign);
        surveyRepository.save(survey);
        log.info(
                "Created new survey with id {} for guid '{}', contextId '{}' and campaignId '{}'",
                survey.getId(),
                survey.getGuid(),
                survey.getContextId(),
                campaign.getId()
        );
    }
}
