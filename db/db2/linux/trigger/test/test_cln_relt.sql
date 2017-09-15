--==========================
-- TEST TRIGGERS: CLN_RELT:
--==========================

-------------
-- EXISTS: 
-------------

BEGIN ATOMIC

	DECLARE v_tgt_id char(10);
	DECLARE v_src_id char(10);
	DECLARE v_op     char(1);
	DECLARE v_chg    char(1);

	SET v_tgt_id = 'Aaqj06L00h';
	SET v_src_id = 'AattSTp01T'; -- test re-insert
	
	UPDATE CWSINT.CLN_RELT r
	SET 
	    r.SAME_HM_CD   = 'Y',
	    r.LST_UPD_ID   = '0x5',
	    r.LST_UPD_TS   = current timestamp
	WHERE r.IDENTIFIER = v_tgt_id ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.SAME_HM_CD FROM CWSRS1.CLN_RELT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'U' OR v_chg != 'Y') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: update existing';
	END IF;

	DELETE FROM CWSINT.CLN_RELT r WHERE r.IDENTIFIER = v_tgt_id ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.SAME_HM_CD FROM CWSRS1.CLN_RELT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'D' OR v_chg != 'Y') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: delete existing';
	END IF;

	INSERT INTO CWSINT.CLN_RELT (IDENTIFIER, ABSENT_CD, CLNTRELC, END_DT, SAME_HM_CD, START_DT, LST_UPD_ID, LST_UPD_TS, FKCLIENT_T, FKCLIENT_0)
	SELECT 
	    v_tgt_id as IDENTIFIER,
		ABSENT_CD, CLNTRELC, END_DT, SAME_HM_CD, START_DT, LST_UPD_ID, LST_UPD_TS, FKCLIENT_T, FKCLIENT_0
	FROM CWSINT.CLN_RELT x
	WHERE x.IDENTIFIER = v_src_id ;
	
	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.SAME_HM_CD FROM CWSRS1.CLN_RELT r WHERE r.IDENTIFIER = v_tgt_id);
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

	SET v_tgt_id = 'Bieber6XXX';
	SET v_src_id = 'AattSTp01T'; -- test re-insert
	
	INSERT INTO CWSINT.CLN_RELT (IDENTIFIER, ABSENT_CD, CLNTRELC, END_DT, SAME_HM_CD, START_DT, LST_UPD_ID, LST_UPD_TS, FKCLIENT_T, FKCLIENT_0)
	SELECT 
	    v_tgt_id as IDENTIFIER,
		ABSENT_CD, CLNTRELC, END_DT, SAME_HM_CD, START_DT, LST_UPD_ID, LST_UPD_TS, FKCLIENT_T, FKCLIENT_0
	FROM CWSINT.CLN_RELT x
	WHERE x.IDENTIFIER = v_src_id ;
		
	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.SAME_HM_CD FROM CWSRS1.CLN_RELT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'I') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: insert new';
	END IF;

	UPDATE CWSINT.CLN_RELT r
	SET 
	    r.SAME_HM_CD   = 'Y',
	    r.LST_UPD_ID   = '0x5',
	    r.LST_UPD_TS   = current timestamp
	WHERE r.IDENTIFIER = v_tgt_id ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.SAME_HM_CD FROM CWSRS1.CLN_RELT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'U' OR v_chg != 'Y') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: update new';
	END IF;

	DELETE FROM CWSINT.CLN_RELT r WHERE r.IDENTIFIER = v_tgt_id ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.SAME_HM_CD FROM CWSRS1.CLN_RELT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'D') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: delete new';
	END IF;

END;


rollback;




