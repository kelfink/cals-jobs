--==========================
-- TEST TRIGGERS: OTH_ADLT:
--==========================

-------------
-- EXISTS: 
-------------

BEGIN ATOMIC

	DECLARE v_tgt_id char(10);
	DECLARE v_src_id char(10);
	DECLARE v_op     char(1);
	DECLARE v_chg    char(1);

	SET v_tgt_id = '02f1gta0OU';
	SET v_src_id = '0ETh2m60OU'; -- test re-insert
	
	UPDATE CWSINT.OTH_ADLT r
	SET 
	    r.GENDER_CD   = 'M',
	    r.LST_UPD_ID   = '0x5',
	    r.LST_UPD_TS   = current timestamp
	WHERE r.IDENTIFIER = v_tgt_id ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.GENDER_CD FROM CWSRS1.OTH_ADLT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'U' OR v_chg != 'M') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: update existing';
	END IF;

	DELETE FROM CWSINT.OTH_ADLT r WHERE r.IDENTIFIER = v_tgt_id ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.GENDER_CD FROM CWSRS1.OTH_ADLT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'D' OR v_chg != 'M') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: delete existing';
	END IF;

	INSERT INTO CWSINT.OTH_ADLT (IDENTIFIER,BIRTH_DT,END_DT,GENDER_CD,OTH_ADLTNM,START_DT,LST_UPD_ID,LST_UPD_TS,FKPLC_HM_T,COMNT_DSC,OTH_ADL_CD,IDENTFD_DT,RESOST_IND,PASSBC_CD)
	SELECT 
	    v_tgt_id as IDENTIFIER,
		BIRTH_DT,END_DT,GENDER_CD,OTH_ADLTNM,START_DT,LST_UPD_ID,LST_UPD_TS,FKPLC_HM_T,COMNT_DSC,OTH_ADL_CD,IDENTFD_DT,RESOST_IND,PASSBC_CD
	FROM CWSINT.OTH_ADLT x
	WHERE x.IDENTIFIER = v_src_id ;
	
	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.GENDER_CD FROM CWSRS1.OTH_ADLT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'I') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: insert existing';
	END IF;

END;


rollback;


-------------
-- NEW: 
-------------

BEGIN ATOMIC

	DECLARE v_tgt_id  char(10);
	DECLARE v_src_id  char(10);
	DECLARE v_op      char(1);
	DECLARE v_chg     char(1);

	SET v_tgt_id = '02iU6I6XXX';
	SET v_src_id = '0ETh2m60OU'; -- test re-insert
	
	INSERT INTO CWSINT.OTH_ADLT (IDENTIFIER,BIRTH_DT,END_DT,GENDER_CD,OTH_ADLTNM,START_DT,LST_UPD_ID,LST_UPD_TS,FKPLC_HM_T,COMNT_DSC,OTH_ADL_CD,IDENTFD_DT,RESOST_IND,PASSBC_CD)
	SELECT 
	    v_tgt_id as IDENTIFIER,
		BIRTH_DT,END_DT,GENDER_CD,OTH_ADLTNM,START_DT,LST_UPD_ID,LST_UPD_TS,FKPLC_HM_T,COMNT_DSC,OTH_ADL_CD,IDENTFD_DT,RESOST_IND,PASSBC_CD
	FROM CWSINT.OTH_ADLT x
	WHERE x.IDENTIFIER = v_src_id ;
		
	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.GENDER_CD FROM CWSRS1.OTH_ADLT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'I') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: insert new';
	END IF;

	UPDATE CWSINT.OTH_ADLT r
	SET 
	    r.GENDER_CD   = 'M',
	    r.LST_UPD_ID   = '0x5',
	    r.LST_UPD_TS   = current timestamp
	WHERE r.IDENTIFIER = v_tgt_id ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.GENDER_CD FROM CWSRS1.OTH_ADLT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'U' OR v_chg != 'M') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: update new';
	END IF;

	DELETE FROM CWSINT.OTH_ADLT r WHERE r.IDENTIFIER = v_tgt_id ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.GENDER_CD FROM CWSRS1.OTH_ADLT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'D') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: delete new';
	END IF;

END;


rollback;




