-- DROP VIEW VW_INTAKE_LOV ;

CREATE VIEW VW_INTAKE_LOV (
	LG_META,
	INTAKE_TYPE,
	PARENT_CAT_ID,
	PARENT_INTAKE_TYPE,
	LG_SYS_ID,
	PARENT_SYS_ID,
	LG_LOG_ID,
	USE_LOG_ID,
	LG_CAT_ID,
	LG_SHRT_DSC,
	LG_LNG_DSC,
	LG_OTH_CD,
	INTAKE_CODE,
	INTAKE_DISPLAY
) AS
SELECT 
lower(ct.lg_meta) as lg_meta,
lower(ct.intake_type) as intake_type, 
par_ct.cat_id as PARENT_CAT_ID, 
lower(par_ct.INTAKE_TYPE) as PARENT_INTAKE_TYPE, 
cd.lg_sys_id, 
cd.parent_lg_sys_id, 
sc.logical_id as LG_LOG_ID,
ct.use_log_id, 
sc.category_id as LG_CAT_ID,
trim(sc.description) as LG_SHRT_DSC,
trim(sc.sub_category_description) as LG_LNG_DSC,
sc.other_code as LG_OTH_CD,
cd.intake_code,
trim(cd.intake_display) as intake_display
--,cd.omit_ind
FROM intake_lov_category      ct
JOIN intake_lov_code          cd     ON ct.cat_id         = cd.cat_id
JOIN system_codes             sc     ON sc."id"           = cd.lg_sys_id
LEFT JOIN intake_lov_code     par_cd ON par_cd.lg_sys_id  = cd.parent_lg_sys_id
LEFT JOIN intake_lov_category par_ct ON par_ct.cat_id     = ct.parent_cat_id
WHERE cd.omit_ind = false
UNION ALL
SELECT 
null as lg_meta, 
ict.intake_type, 
null as PARENT_CAT_ID, 
null as PARENT_INTAKE_TYPE, 
null as lg_sys_id, 
null as parent_lg_sys_id, 
icd.intake_code as LG_LOG_ID,
true as use_log_id, 
null as LG_CAT_ID,
null as LG_SHRT_DSC,
null as LG_LNG_DSC,
null as LG_OTH_CD,
icd.intake_code,
icd.intake_display
--,icd.omit_ind
FROM intake_only_lov_category  ict
JOIN intake_only_lov_code      icd ON ict.cat_id = icd.cat_id
WHERE icd.omit_ind = false
;


GRANT SELECT ON VW_INTAKE_LOV to PUBLIC;

