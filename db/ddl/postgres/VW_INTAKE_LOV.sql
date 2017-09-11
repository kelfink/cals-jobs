DROP VIEW VW_INTAKE_LOV ;

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
	ct.lg_meta, 
	ct.intake_type, 
	par_ct.cat_id as PARENT_CAT_ID, 
	par_ct.INTAKE_TYPE as PARENT_INTAKE_TYPE, 
	cd.lg_sys_id, 
	cd.parent_lg_sys_id, 
	sc.logical_id as LG_LOG_ID,
	ct.use_log_id, 
	sc.category_id as LG_CAT_ID,
	sc.description as LG_SHRT_DSC,
	sc.sub_category_description as LG_LNG_DSC,
	sc.other_code as LG_OTH_CD,
	cd.intake_code,
	cd.intake_display
	FROM intake_lov_category      ct
	JOIN intake_lov_code          cd     ON ct.cat_id         = cd.cat_id
	JOIN system_codes             sc     ON sc."id"           = cd.lg_sys_id
	LEFT JOIN intake_lov_code     par_cd ON par_cd.lg_sys_id  = cd.parent_lg_sys_id
	LEFT JOIN intake_lov_category par_ct ON par_ct.cat_id     = ct.parent_cat_id
;


GRANT SELECT ON VW_INTAKE_LOV to PUBLIC;

