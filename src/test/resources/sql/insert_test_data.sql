TRUNCATE TABLE campaign;

INSERT INTO campaign (id, name, survey_type_code, trigger_type, delay_hours, active, start_date, deleted, create_time, create_user, last_update_time, last_update_user)
VALUES
('8137a4ee-1757-48ba-8dc0-c7a58415218f', 'NPS после заказа', 'NPS', 'ORDER_COMPLETED', 24, TRUE, '2024-01-01 00:00:00'::timestamp, FALSE, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
('63ea9448-5ac7-4864-a881-e4dcf8199b18', 'CSAT после обращения в поддержку', 'CSAT', 'TICKET_CLOSED', 0, TRUE, '2024-01-01 00:00:00'::timestamp, FALSE, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system');

TRUNCATE TABLE survey_type;

INSERT INTO survey_type (code, name, question, scale_min, scale_max, formula_description, create_time, create_user, last_update_time, last_update_user)
VALUES
    ('NPS', 'Net Promoter Score', 'Оцените от 0 до 10, порекомендуете ли вы нас?', 0, 10, '(promoters - detractors) / total * 100', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('CSAT', 'Customer Satisfaction Score', 'Оцените качество обслуживания', 1, 5, 'satisfied (4-5) / total * 100', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('CES', 'Customer Effort Score', 'Насколько легко было решить вопрос?', 1, 5, 'SUM(score) / total', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system');

-- Вставка тестовых данных в таблицу survey
TRUNCATE TABLE survey;

INSERT INTO survey (id, campaign_id, guid, context_type, context_id, status, scheduled_at, sent_at, completed_at, create_time, create_user, last_update_time, last_update_user)
VALUES
    ('f47ac10b-58cc-4372-a567-0e02b2c3d479','8137a4ee-1757-48ba-8dc0-c7a58415218f','customer-001', 'ORDER', 'order-12345', 'PENDING', '2026-02-01 10:00:00', '2026-02-01 10:05:00', '2026-02-02 14:30:00', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 'admin'),
    ('550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440102', 'customer-002', 'TICKET', 'ticket-67890', 'PENDING', '2026-02-05 09:00:00', '2026-02-05 09:15:00', NULL, CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 'admin'),
    ('7c3e1e6d-1b3f-4e4f-8b4b-6b1b1b1b1b1b', '550e8400-e29b-41d4-a716-446655440103', 'customer-003', 'ORDER', 'order-54321', 'COMPLETED', '2026-02-03 14:00:00', '2026-02-03 14:20:00', '2026-02-04 11:45:00', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
    ('550e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440104', 'customer-004', 'TICKET', 'ticket-11111', 'EXPIRED', '2026-01-25 08:00:00', '2026-01-25 08:30:00', NULL, CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, 'admin'),
    ('550e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440105', 'customer-005', 'ORDER', 'order-99999', 'PENDING', '2026-02-11 12:00:00', NULL, NULL, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system');

