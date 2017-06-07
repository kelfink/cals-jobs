CREATE VIEW CWSRS1.VW_BI_DIR_RELATION (
    REVERSE_RELATIONSHIP,
	THIS_LEGACY_ID,
	THIS_FIRST_NAME,
	THIS_LAST_NAME,
	REL_CODE,
	RELATED_LEGACY_ID,
	RELATED_FIRST_NAME,
	RELATED_LAST_NAME,
	LAST_CHG
) AS 
SELECT 
	0 as REVERSE_RELATIONSHIP,
	v.THIS_LEGACY_ID,
	v.THIS_FIRST_NAME,
	v.THIS_LAST_NAME,
	v.REL_CODE,
	v.RELATED_LEGACY_ID,
	v.RELATED_FIRST_NAME,
	v.RELATED_LAST_NAME,
	v.LAST_CHG
FROM CWSRS1.ES_REL_CLN_RELT_CLIENT v
UNION ALL
SELECT 
	1 as REVERSE_RELATIONSHIP,
	v.RELATED_LEGACY_ID,
	v.RELATED_FIRST_NAME,
	v.RELATED_LAST_NAME,
	v.REL_CODE,
	v.THIS_LEGACY_ID,
	v.THIS_FIRST_NAME,
	v.THIS_LAST_NAME,
	v.LAST_CHG
FROM CWSRS1.ES_REL_CLN_RELT_CLIENT v
;
