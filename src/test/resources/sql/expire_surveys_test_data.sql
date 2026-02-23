DELETE FROM customer_feedback;
DELETE FROM survey;
DELETE FROM campaign;
DELETE FROM survey_type;

INSERT INTO survey_type (code, name, question, scale_min, scale_max, formula_description) VALUES
('NPS', 'Net Promoter Score', 'How likely are you to recommend us?', 0, 10, 'Promoters - Detractors'),
('CES', 'Customer Effort Score', 'How easy was it to interact with us?', 1, 5, 'Average score');

INSERT INTO campaign (id, name, survey_type_code, trigger_type, delay_hours, active, start_date, end_date, deleted) VALUES
('a1a1a1a1-1a1a-1a1a-1a1a-1a1a1a1a1a1a', 'Active Campaign', 'NPS', 'MANUAL', 0, true, NOW() - INTERVAL '20 day', NULL, false),
('a2a2a2a2-2a2a-2a2a-2a2a-2a2a2a2a2a2a', 'Ended Campaign', 'CES', 'MANUAL', 0, true, NOW() - INTERVAL '40 day', NOW() - INTERVAL '1 day', false);

INSERT INTO survey (id, campaign_id, guid, context_type, context_id, status, scheduled_at, sent_at, create_time)
VALUES  ('c1c1c1c1-1c1c-1c1c-1c1c-1c1c1c1c1c13', 'a2a2a2a2-2a2a-2a2a-2a2a-2a2a2a2a2a2a', 'user-campaign-ended', 'TICKET', 't-1', 'PENDING', NOW() - INTERVAL '5 day', NOW() - INTERVAL '2 day', NOW() - INTERVAL '5 day'),
        ('c1c1c1c1-1c1c-1c1c-1c1c-1c1c1c1c1c14', 'a1a1a1a1-1a1a-1a1a-1a1a-1a1a1a1a1a1a', 'user-sent-recent', 'ORDER', 'ord-3', 'PENDING', NOW() - INTERVAL '2 day', NOW() - INTERVAL '1 day', NOW() - INTERVAL '2 day'),
        ('c1c1c1c1-1c1c-1c1c-1c1c-1c1c1c1c1c15', 'a1a1a1a1-1a1a-1a1a-1a1a-1a1a1a1a1a1a', 'user-unsent-recent', 'ORDER', 'ord-4', 'PENDING', NOW() - INTERVAL '30 day', NULL, NOW() - INTERVAL '30 day'),
        ('c1c1c1c1-1c1c-1c1c-1c1c-1c1c1c1c1c12', 'a1a1a1a1-1a1a-1a1a-1a1a-1a1a1a1a1a1a', 'user-unsent-expired', 'ORDER', 'ord-2', 'PENDING', NOW() - INTERVAL '61 day', NULL, NOW() - INTERVAL '61 day'),
        ('c1c1c1c1-1c1c-1c1c-1c1c-1c1c1c1c1c16', 'a1a1a1a1-1a1a-1a1a-1a1a-1a1a1a1a1a1a', 'user-completed', 'ORDER', 'ord-5', 'COMPLETED', NOW() - INTERVAL '15 day', NOW() - INTERVAL '14 day', NOW() - INTERVAL '15 day'),
        ('c1c1c1c1-1c1c-1c1c-1c1c-1c1c1c1c1c11', 'a1a1a1a1-1a1a-1a1a-1a1a-1a1a1a1a1a1a', 'user-sent-expired', 'ORDER', 'ord-1', 'PENDING', NOW() - INTERVAL '10 day', NOW() - INTERVAL '8 day', NOW() - INTERVAL '10 day');