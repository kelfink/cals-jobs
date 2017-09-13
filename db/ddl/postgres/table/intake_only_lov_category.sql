CREATE TABLE intake_only_lov_category (
	CAT_ID			   serial   NOT NULL,
	INTAKE_TYPE		   varchar	NOT NULL,
	CAT_DESCR    	   varchar,
	PRIMARY KEY (CAT_ID)
);

GRANT SELECT ON intake_only_lov_category to PUBLIC;
