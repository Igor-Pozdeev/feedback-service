INSERT INTO survey_type (code, name, question, scale_min, scale_max, formula_description, create_time, create_user, last_update_time, last_update_user)
VALUES
('NPS', 'Net Promoter Score', 'Оцените от 0 до 10, порекомендуете ли вы нас?', 0, 10, '(promoters - detractors) / total * 100', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
('CSAT', 'Customer Satisfaction Score', 'Оцените качество обслуживания', 1, 5, 'satisfied (4-5) / total * 100', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
('CES', 'Customer Effort Score', 'Насколько легко было решить вопрос?', 1, 5, 'SUM(score) / total', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system');
