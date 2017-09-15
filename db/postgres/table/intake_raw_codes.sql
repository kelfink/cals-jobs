-- TRUNCATE TABLE postgres.public.intake_raw_codes CASCADE ;

-- DROP TABLE intake_raw_codes;

CREATE TABLE intake_raw_codes (
	lg_sys_id          int4    NOT NULL,
	lg_meta            varchar NOT NULL,
	lg_shrt_dsc        varchar NOT NULL,
	lg_log_id          varchar,
	lg_inactive        varchar NOT NULL,
	lg_cat_id          varchar,
	lg_oth_cd          varchar,
	lg_lng_dsc         varchar,
	intake_type        varchar NOT NULL,
	intake_code        varchar NOT NULL,
	intake_display     varchar,
	use_log_id         bool,
	parent_sys_id      int4,
	parent_intake_type varchar,
	omit_ind		   boolean NOT NULL,
	PRIMARY KEY (lg_sys_id,intake_type,intake_code)
);


GRANT SELECT ON intake_raw_codes to PUBLIC;
