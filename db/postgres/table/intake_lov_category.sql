-- TRUNCATE TABLE postgres.public.intake_lov_category CASCADE ;

-- DROP TABLE intake_lov_category CASCADE ;

CREATE TABLE intake_lov_category (
	CAT_ID			   serial   NOT NULL,
	LG_META			   varchar	NOT NULL,
	INTAKE_TYPE		   varchar	NOT NULL,
	PARENT_CAT_ID	   int4,
	USE_LOG_ID		   boolean DEFAULT false,
	CAT_DESCR    	   varchar,
	PRIMARY KEY (CAT_ID)
);


CREATE UNIQUE INDEX iux_intake_category  ON intake_lov_category USING btree (LG_META, INTAKE_TYPE);
CREATE UNIQUE INDEX iux_intake_category2 ON intake_lov_category USING btree (INTAKE_TYPE);


GRANT SELECT ON intake_lov_category to PUBLIC;
