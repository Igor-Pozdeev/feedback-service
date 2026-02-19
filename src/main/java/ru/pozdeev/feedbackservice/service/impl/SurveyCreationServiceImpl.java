package ru.pozdeev.feedbackservice.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pozdeev.feedbackservice.dto.event.CustomerEvent;
import ru.pozdeev.feedbackservice.mapper.SurveyMapper;
import ru.pozdeev.feedbackservice.model.Campaign;
import ru.pozdeev.feedbackservice.model.Survey;
import ru.pozdeev.feedbackservice.model.TriggerType;
import ru.pozdeev.feedbackservice.repository.CampaignRepository;
import ru.pozdeev.feedbackservice.repository.SurveyRepository;
import ru.pozdeev.feedbackservice.service.SurveyCreationService;

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
        TriggerType triggerType = event.eventType();
        List<Campaign> activeCampaigns = campaignRepository.findAllByTriggerTypeAndActiveIsTrue(triggerType);

        activeCampaigns.stream()
                .filter(campaign -> !surveyRepository.existsByContextIdAndCampaignId(event.contextId(), campaign.getId()))
                .forEach(campaign -> saveSurvey(event, campaign));
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
