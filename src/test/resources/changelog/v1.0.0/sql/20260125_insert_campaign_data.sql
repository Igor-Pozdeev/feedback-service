INSERT INTO campaign (id, name, survey_type_code, trigger_type, delay_hours, active, start_date, deleted, create_time, create_user, last_update_time, last_update_user)
VALUES
('8137a4ee-1757-48ba-8dc0-c7a58415217f', 'NPS после заказа', 'NPS', 'ORDER_COMPLETED', 24, TRUE, '2024-01-01 00:00:00'::timestamp, FALSE, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
('63ea9448-5ac7-4864-a881-e4dcf8199b08', 'CSAT после обращения в поддержку', 'CSAT', 'TICKET_CLOSED', 0, TRUE, '2024-01-01 00:00:00'::timestamp, FALSE, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system');
