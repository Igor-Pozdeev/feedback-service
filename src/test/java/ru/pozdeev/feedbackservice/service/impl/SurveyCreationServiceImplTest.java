package ru.pozdeev.feedbackservice.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pozdeev.feedbackservice.dto.event.CustomerEvent;
import ru.pozdeev.feedbackservice.mapper.SurveyMapper;
import ru.pozdeev.feedbackservice.model.Campaign;
import ru.pozdeev.feedbackservice.model.ContextType;
import ru.pozdeev.feedbackservice.model.Survey;
import ru.pozdeev.feedbackservice.model.TriggerType;
import ru.pozdeev.feedbackservice.repository.CampaignRepository;
import ru.pozdeev.feedbackservice.repository.SurveyRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SurveyCreationServiceImplTest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private SurveyRepository surveyRepository;

    @Mock
    private SurveyMapper surveyMapper;

    @InjectMocks
    private SurveyCreationServiceImpl surveyCreationService;

    @Test
    void processCustomerEvent_shouldCreateSurvey_whenActiveCampaignExistsAndNoDuplicate() {
        // given
        CustomerEvent event = new CustomerEvent(TriggerType.ORDER_COMPLETED, "guid", "context_id", ContextType.ORDER, LocalDateTime.now(), Map.of("order_total", "some order"));
        Campaign campaign = new Campaign();
        UUID campaignId = UUID.randomUUID();
        campaign.setId(campaignId);
        campaign.setDelayHours(1);
        campaign.setTriggerType(TriggerType.ORDER_COMPLETED);

        when(campaignRepository.findAllByTriggerTypeAndActiveIsTrue(TriggerType.ORDER_COMPLETED)).thenReturn(List.of(campaign));
        when(surveyRepository.existsByContextIdAndCampaignId("context_id", campaignId)).thenReturn(false);
        when(surveyMapper.toSurvey(event, campaign)).thenReturn(Survey.builder().id(UUID.randomUUID()).campaignId(campaignId).build());

        // when
        surveyCreationService.processCustomerEvent(event);

        // then
        verify(surveyRepository).save(any(Survey.class));
    }

    @Test
    void processCustomerEvent_shouldNotCreateSurvey_whenDuplicateExists() {
        // given
        CustomerEvent event = new CustomerEvent(TriggerType.ORDER_COMPLETED, "guid", "context_id", ContextType.ORDER, LocalDateTime.now(), Map.of("order_total", "some order"));
        Campaign campaign = new Campaign();
        UUID campaignId = UUID.randomUUID();
        campaign.setId(campaignId);
        campaign.setTriggerType(TriggerType.ORDER_COMPLETED);

        when(campaignRepository.findAllByTriggerTypeAndActiveIsTrue(TriggerType.ORDER_COMPLETED)).thenReturn(List.of(campaign));
        when(surveyRepository.existsByContextIdAndCampaignId("context_id", campaignId)).thenReturn(true);

        // when
        surveyCreationService.processCustomerEvent(event);

        // then
        verify(surveyRepository, never()).save(any(Survey.class));
    }

    @Test
    void processCustomerEvent_shouldCreateMultipleSurveys_forMultipleCampaigns() {
        // given
        CustomerEvent event = new CustomerEvent(TriggerType.TICKET_CLOSED, "guid", "context_id", ContextType.TICKET, LocalDateTime.now(), Map.of("resolution_time_hours", "some hours"));
        Campaign campaign1 = new Campaign();
        UUID campaign1Id = UUID.randomUUID();
        campaign1.setId(campaign1Id);
        campaign1.setDelayHours(1);
        campaign1.setTriggerType(TriggerType.TICKET_CLOSED);
        Campaign campaign2 = new Campaign();
        UUID campaign2Id = UUID.randomUUID();
        campaign2.setId(campaign2Id);
        campaign2.setDelayHours(2);
        campaign2.setTriggerType(TriggerType.TICKET_CLOSED);

        when(campaignRepository.findAllByTriggerTypeAndActiveIsTrue(TriggerType.TICKET_CLOSED)).thenReturn(List.of(campaign1, campaign2));
        when(surveyRepository.existsByContextIdAndCampaignId("context_id", campaign1Id)).thenReturn(false);
        when(surveyRepository.existsByContextIdAndCampaignId("context_id", campaign2Id)).thenReturn(false);
        when(surveyMapper.toSurvey(event, campaign1)).thenReturn(Survey.builder().id(UUID.randomUUID()).campaignId(campaign1Id).build());
        when(surveyMapper.toSurvey(event, campaign2)).thenReturn(Survey.builder().id(UUID.randomUUID()).campaignId(campaign2Id).build());

        // when
        surveyCreationService.processCustomerEvent(event);

        // then
        verify(surveyRepository, times(2)).save(any(Survey.class));
    }

    @Test
    void processCustomerEvent_shouldNotCreateSurvey_whenNoActiveCampaign() {
        // given
        CustomerEvent event = new CustomerEvent(TriggerType.ORDER_COMPLETED, "guid", "context_id", ContextType.ORDER, LocalDateTime.now(), Map.of("order_total", "some order"));

        when(campaignRepository.findAllByTriggerTypeAndActiveIsTrue(TriggerType.ORDER_COMPLETED)).thenReturn(Collections.emptyList());

        // when
        surveyCreationService.processCustomerEvent(event);

        // then
        verify(surveyRepository, never()).save(any(Survey.class));
    }
}
