-- DROP VIEW VW_SCREENING_HISTORY ;


CREATE VIEW VW_SCREENING_HISTORY AS (
SELECT
	s."id"      AS screening_id,
	alg."id"    AS allegation_id,
	p."id"      AS participant_id,
	p.legacy_id AS person_legacy_id,
	alg.allegation_types,
	CASE WHEN p."id" = alg.victim_id      THEN 1 END AS is_alg_victim,
	CASE WHEN p."id" = alg.perpetrator_id THEN 1 END AS is_alg_perp,
	s.referral_id,
	s.reference,
	s.started_at,
	s.ended_at,
	s.incident_date,
	s.location_type,
	s.communication_method,
	s."name" AS screening_name,
	s.screening_decision,
	s.incident_county,
	s.report_narrative,
	s.assignee,
	s.additional_information,
	s.screening_decision_detail,
	p.date_of_birth AS birth_dt,
	p.first_name,
	p.last_name,
	p.gender,
	p.ssn,
	p.roles,
	CASE WHEN (array_position(p.roles, 'Mandated Reporter')     IS NOT NULL 
	        OR array_position(p.roles, 'Non-mandated Reporter') IS NOT NULL 
	        OR array_position(p.roles, 'Anonymous Reporter')    IS NOT NULL) THEN 1 ELSE 0 END AS is_reporter,
	CASE WHEN p."id" = alg.victim_id      OR array_position(p.roles, 'Victim')      IS NOT NULL THEN 1 ELSE 0 END AS is_victim,
	CASE WHEN p."id" = alg.perpetrator_id OR array_position(p.roles, 'Perpetrator') IS NOT NULL THEN 1 ELSE 0 END AS is_perpetrator,
	pa."id"           AS partc_addr_id,
	pa.participant_id AS pa_partc_id,
	adr."id"          AS address_id,
	adr."type"        AS address_type, 
	adr.street_address, 
	adr.city, 
	adr."state", 
	adr.zip,
	ppn.phone_number_id, 
	ppn.participant_id AS ph_partc_id,
	ph."number"        AS phone_number, 
	ph."type"          AS phone_type,
	(SELECT MAX(v.created_at) FROM versions v WHERE
		(v.item_type = 'Participant'            AND   p."id" = CAST(v.item_id AS varchar))
	 OR (v.item_type = 'Address'                AND adr."id" = CAST(v.item_id AS varchar))
	 OR (v.item_type = 'Allegation'             AND alg."id" = v.item_id)
	 OR (v.item_type = 'ParticipantAddress'     AND  pa."id" = CAST(v.item_id AS varchar))
	 OR (v.item_type = 'ParticipantPhoneNumber' AND ppn."id" = CAST(v.item_id AS varchar))
	 OR (v.item_type = 'PhoneNumber'            AND  ph."id" = CAST(v.item_id AS varchar))
	 OR (v.item_type = 'Screening'              AND   s."id" = CAST(v.item_id AS varchar))
	) AS last_chg
FROM            screenings s
LEFT OUTER JOIN allegations alg               ON alg.screening_id = s."id"
LEFT OUTER JOIN participants p                ON p.screening_id = s."id" OR p."id" = alg.victim_id OR p."id" = alg.perpetrator_id
LEFT OUTER JOIN participant_addresses pa      ON pa.participant_id = p."id"
LEFT OUTER JOIN addresses adr                 ON adr."id" = pa.address_id AND COALESCE(adr.street_address, adr.city, adr.zip) IS NOT NULL
LEFT OUTER JOIN participant_phone_numbers ppn ON ppn.participant_id = p."id"
LEFT OUTER JOIN phone_numbers ph              ON ph."id" = ppn.phone_number_id
);


--==========================
-- SAMPLE USAGE:
--==========================

SELECT p."id" as ns_partc_id, p.legacy_id as cms_legacy_id, vw.*
FROM VW_SCREENING_HISTORY vw
JOIN participants p ON p.screening_id = vw.screening_id
WHERE vw.referral_id IS NOT NULL
--WHERE p.first_name = 'Sally'
--WHERE p.legacy_id = 'PW5exD60S0'
FOR READ ONLY ;


--==========================
-- SCREENING: LAST CHG:
--==========================

SELECT p."id" AS ns_partc_id, p.legacy_id AS cms_legacy_id, vw.*
FROM VW_SCREENING_HISTORY vw
JOIN participants p on p.screening_id = vw.screening_id
WHERE vw.participant_id IN (
	SELECT DISTINCT vw1.participant_id
	FROM VW_SCREENING_HISTORY vw1
	WHERE vw1.LAST_CHG > TO_TIMESTAMP('01 Mar 2017 14:00', 'DD Mon YYYY HH24:MI') 
)
AND p.legacy_id IS NOT NULL
ORDER BY cms_legacy_id, screening_id, person_legacy_id, participant_id
FOR READ ONLY ;

