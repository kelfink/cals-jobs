--==========================
-- TEST TRIGGERS: OTH_KIDT:
--==========================

-------------
-- EXISTS: 
-------------

BEGIN ATOMIC

	DECLARE v_tgt_id char(10);
	DECLARE v_src_id char(10);
	DECLARE v_op     char(1);
	DECLARE v_chg    char(1);

	SET v_tgt_id = 'AbLfD9NCON';
	SET v_src_id = 'Ae3QVrp0FG'; -- test re-insert
	
	UPDATE CWSINT.OTH_KIDT r
	SET 
	    r.GENDER_CD   = 'M',
	    r.LST_UPD_ID   = '0x5',
	    r.LST_UPD_TS   = current timestamp
	WHERE r.IDENTIFIER = v_tgt_id ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.GENDER_CD FROM CWSRS1.OTH_KIDT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'U' OR v_chg != 'M') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: update existing';
	END IF;

	DELETE FROM CWSINT.OTH_KIDT r WHERE r.IDENTIFIER = v_tgt_id ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.GENDER_CD FROM CWSRS1.OTH_KIDT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'D' OR v_chg != 'M') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: delete existing';
	END IF;

	INSERT INTO CWSINT.OTH_KIDT (IDENTIFIER,BIRTH_DT,GENDER_CD,OTHCHLD_NM,LST_UPD_ID,LST_UPD_TS,FKPLC_HM_T,YR_INC_AMT)
	SELECT 
	    v_tgt_id as IDENTIFIER,
		BIRTH_DT,GENDER_CD,OTHCHLD_NM,LST_UPD_ID,LST_UPD_TS,FKPLC_HM_T,YR_INC_AMT
	FROM CWSINT.OTH_KIDT x
	WHERE x.IDENTIFIER = v_src_id ;
	
	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.GENDER_CD FROM CWSRS1.OTH_KIDT r WHERE r.IDENTIFIER = v_tgt_id);
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
	SET v_src_id = 'Ae3QVrp0FG'; -- test re-insert
	
	INSERT INTO CWSINT.OTH_KIDT (IDENTIFIER,BIRTH_DT,GENDER_CD,OTHCHLD_NM,LST_UPD_ID,LST_UPD_TS,FKPLC_HM_T,YR_INC_AMT)
	SELECT 
	    v_tgt_id as IDENTIFIER,
		BIRTH_DT,GENDER_CD,OTHCHLD_NM,LST_UPD_ID,LST_UPD_TS,FKPLC_HM_T,YR_INC_AMT
	FROM CWSINT.OTH_KIDT x
	WHERE x.IDENTIFIER = v_src_id ;
		
	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.GENDER_CD FROM CWSRS1.OTH_KIDT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'I') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: insert new';
	END IF;

	UPDATE CWSINT.OTH_KIDT r
	SET 
	    r.GENDER_CD   = 'M',
	    r.LST_UPD_ID   = '0x5',
	    r.LST_UPD_TS   = current timestamp
	WHERE r.IDENTIFIER = v_tgt_id ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.GENDER_CD FROM CWSRS1.OTH_KIDT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'U' OR v_chg != 'M') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: update new';
	END IF;

	DELETE FROM CWSINT.OTH_KIDT r WHERE r.IDENTIFIER = v_tgt_id ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.GENDER_CD FROM CWSRS1.OTH_KIDT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'D') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: delete new';
	END IF;

END;


rollback;




