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

