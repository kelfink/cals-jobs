--==========================
-- TEST TRIGGERS: REFR_CLT:
--==========================

-------------
-- EXISTS: 
-------------

BEGIN ATOMIC

	DECLARE v_tgt_fkclient  char(10);
	DECLARE v_tgt_fkreferl_t char(10);

	DECLARE v_src_fkclient   char(10);
	DECLARE v_src_fkreferl_t char(10);

	DECLARE v_op     char(1);
	DECLARE v_chg    char(6);

	SET v_tgt_fkclient   = 'AapJGAU04Z';
	SET v_tgt_fkreferl_t = 'T6g4R8604Z';
	
	SET v_src_fkclient   = 'Abi5qGw04Z';
	SET v_src_fkreferl_t = 'Coybyba04Z';
	
	UPDATE CWSINT.REFR_CLT r
	SET 
	    r.DISPSTN_CD   = 'Y',
	    r.LST_UPD_ID   = '0x5',
	    r.LST_UPD_TS   = current timestamp
	WHERE r.FKCLIENT_T = v_tgt_fkclient AND r.FKREFERL_T = v_tgt_fkreferl_t;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.DISPSTN_CD FROM CWSRS1.REFR_CLT r WHERE r.FKCLIENT_T = v_tgt_fkclient AND r.FKREFERL_T = v_tgt_fkreferl_t);
	IF (v_op != 'U' OR v_chg != 'Y') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: update existing';
	END IF;

 	DELETE FROM CWSINT.REFR_CLT r WHERE r.FKCLIENT_T = v_tgt_fkclient AND r.FKREFERL_T = v_tgt_fkreferl_t ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.DISPSTN_CD FROM CWSRS1.REFR_CLT r WHERE r.FKCLIENT_T = v_tgt_fkclient AND r.FKREFERL_T = v_tgt_fkreferl_t);
	IF (v_op != 'D' OR v_chg != 'Y') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: delete existing';
	END IF;

	INSERT INTO CWSINT.REFR_CLT (FKREFERL_T,FKCLIENT_T,APRVL_NO,APV_STC,DSP_RSNC,DISPSTN_CD,RCL_DISPDT,SLFRPT_IND,STFADD_IND,LST_UPD_ID,LST_UPD_TS,DSP_CLSDSC,RFCL_AGENO,AGE_PRD_CD,CNTY_SPFCD,MHLTH_IND,ALCHL_IND,DRUG_IND)
	SELECT 
	    v_tgt_fkreferl_t as FKREFERL_T, v_tgt_fkclient as FKCLIENT_T, APRVL_NO,APV_STC,DSP_RSNC,DISPSTN_CD,RCL_DISPDT,SLFRPT_IND,STFADD_IND,LST_UPD_ID,LST_UPD_TS,DSP_CLSDSC,RFCL_AGENO,AGE_PRD_CD,CNTY_SPFCD,MHLTH_IND,ALCHL_IND,DRUG_IND
	FROM CWSINT.REFR_CLT r
	WHERE r.FKCLIENT_T = v_src_fkclient AND r.FKREFERL_T = v_src_fkreferl_t;
	
	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.DISPSTN_CD FROM CWSRS1.REFR_CLT r WHERE r.FKCLIENT_T = v_tgt_fkclient AND r.FKREFERL_T = v_tgt_fkreferl_t);
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

	DECLARE v_tgt_fkclient char(10);
	DECLARE v_tgt_fkreferl_t char(10);

	DECLARE v_src_fkclient char(10);
	DECLARE v_src_fkreferl_t char(10);

	DECLARE v_op     char(1);
	DECLARE v_chg    char(6);

	SET v_tgt_fkclient = '666JYAUXXX';
	SET v_tgt_fkreferl_t = '6664Y86XXX';
	
	SET v_src_fkclient = 'Abi5qGw04Z';
	SET v_src_fkreferl_t = 'Coybyba04Z';
	
	INSERT INTO CWSINT.REFR_CLT (FKREFERL_T, FKCLIENT_T, FIRST_NM, LAST_NM, MIDDLE_NM, DISPSTN_CD, NAME_TPC, SUFX_TLDSC, LST_UPD_ID, LST_UPD_TS)
	SELECT 
	    v_tgt_fkreferl_t as FKREFERL_T, v_tgt_fkclient as FKCLIENT_T, FIRST_NM, LAST_NM, MIDDLE_NM, DISPSTN_CD, NAME_TPC, SUFX_TLDSC, LST_UPD_ID, LST_UPD_TS
	FROM CWSINT.REFR_CLT r
	WHERE r.FKCLIENT_T = v_src_fkclient AND r.FKREFERL_T = v_src_fkreferl_t;
	
	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.DISPSTN_CD FROM CWSRS1.REFR_CLT r WHERE r.FKCLIENT_T = v_tgt_fkclient AND r.FKREFERL_T = v_tgt_fkreferl_t);
	IF (v_op != 'I') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: insert existing';
	END IF;

	UPDATE CWSINT.REFR_CLT r
	SET 
	    r.DISPSTN_CD   = 'Y',
	    r.LST_UPD_ID   = '0x5',
	    r.LST_UPD_TS   = current timestamp
	WHERE r.FKCLIENT_T = v_tgt_fkclient AND r.FKREFERL_T = v_tgt_fkreferl_t;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.DISPSTN_CD FROM CWSRS1.REFR_CLT r WHERE r.FKCLIENT_T = v_tgt_fkclient AND r.FKREFERL_T = v_tgt_fkreferl_t);
	IF (v_op != 'U' OR v_chg != 'Y') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: update existing';
	END IF;

 	DELETE FROM CWSINT.REFR_CLT r WHERE r.FKCLIENT_T = v_tgt_fkclient AND r.FKREFERL_T = v_tgt_fkreferl_t ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.DISPSTN_CD FROM CWSRS1.REFR_CLT r WHERE r.FKCLIENT_T = v_tgt_fkclient AND r.FKREFERL_T = v_tgt_fkreferl_t);
	IF (v_op != 'D' OR v_chg != 'Y') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: delete existing';
	END IF;

	INSERT INTO CWSINT.REFR_CLT (FKREFERL_T,FKCLIENT_T,APRVL_NO,APV_STC,DSP_RSNC,DISPSTN_CD,RCL_DISPDT,SLFRPT_IND,STFADD_IND,LST_UPD_ID,LST_UPD_TS,DSP_CLSDSC,RFCL_AGENO,AGE_PRD_CD,CNTY_SPFCD,MHLTH_IND,ALCHL_IND,DRUG_IND)
	SELECT 
	    v_tgt_fkreferl_t as FKREFERL_T, v_tgt_fkclient as FKCLIENT_T, FIRST_NM, LAST_NM, MIDDLE_NM, DISPSTN_CD, NAME_TPC, SUFX_TLDSC, LST_UPD_ID, LST_UPD_TS
	FROM CWSINT.REFR_CLT r
	WHERE r.FKCLIENT_T = v_src_fkclient AND r.FKREFERL_T = v_src_fkreferl_t;
	
	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.DISPSTN_CD FROM CWSRS1.REFR_CLT r WHERE r.FKCLIENT_T = v_tgt_fkclient AND r.FKREFERL_T = v_tgt_fkreferl_t);
	IF (v_op != 'I') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: insert existing';
	END IF;

END;


rollback;




