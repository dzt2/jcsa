package com.jcsa.jcmuta.mutant.error2mutation;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmuta.MutaClass;
import com.jcsa.jcmuta.MutaOperator;
import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.infection.incr.UIODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.incr.UIOIInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.incr.UIORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.incr.UNODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.incr.UNOIInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.incr.VINCInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.ADDDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.ADDMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.ADDMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.ADDSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.DIVADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.DIVMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.DIVMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.DIVSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.MODADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.MODDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.MODMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.MODSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.MULADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.MULDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.MULMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.MULSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.SUBADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.SUBDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.SUBMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.SUBMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.ADDBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.ADDBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.ADDBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.ADDLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.ADDRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.DIVBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.DIVBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.DIVBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.DIVLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.DIVRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.MODBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.MODBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.MODBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.MODLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.MODRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.MULBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.MULBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.MULBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.MULLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.MULRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.SUBBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.SUBBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.SUBBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.SUBLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.SUBRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaln.ADDLANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaln.ADDLORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaln.DIVLANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaln.DIVLORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaln.MODLANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaln.MODLORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaln.MULLANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaln.MULLORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaln.SUBLANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaln.SUBLORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.ADDEQVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.ADDGREInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.ADDGRTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.ADDNEQInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.ADDSMEInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.ADDSMTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.DIVEQVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.DIVGREInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.DIVGRTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.DIVNEQInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.DIVSMEInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.DIVSMTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.MODEQVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.MODGREInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.MODGRTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.MODNEQInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.MODSMEInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.MODSMTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.MULEQVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.MULGREInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.MULGRTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.MULNEQInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.MULSMEInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.MULSMTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.SUBEQVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.SUBGREInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.SUBGRTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.SUBNEQInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.SUBSMEInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oarn.SUBSMTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.BANADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.BANDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.BANMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.BANMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.BANSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.BORADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.BORDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.BORMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.BORMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.BORSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.BXRADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.BXRDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.BXRMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.BXRMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.BXRSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.LSHADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.LSHDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.LSHMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.LSHMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.LSHSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.RSHADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.RSHDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.RSHMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.RSHMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oban.RSHSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obbn.BANBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obbn.BANBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obbn.BANLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obbn.BANRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obbn.BORBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obbn.BORBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obbn.BORLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obbn.BORRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obbn.BXRBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obbn.BXRBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obbn.BXRLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obbn.BXRRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obbn.LSHBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obbn.LSHBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obbn.LSHBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obbn.LSHRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obbn.RSHBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obbn.RSHBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obbn.RSHBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obbn.RSHLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obln.BANLANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obln.BANLORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obln.BORLANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obln.BORLORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obln.BXRLANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obln.BXRLORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obln.LSHLANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obln.LSHLORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obln.RSHLANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obln.RSHLORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.BANEQVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.BANGREInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.BANGRTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.BANNEQInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.BANSMEInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.BANSMTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.BOREQVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.BORGREInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.BORGRTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.BORNEQInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.BORSMEInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.BORSMTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.BXREQVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.BXRGREInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.BXRGRTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.BXRNEQInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.BXRSMEInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.BXRSMTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.LSHEQVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.LSHGREInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.LSHGRTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.LSHNEQInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.LSHSMEInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.LSHSMTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.RSHEQVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.RSHGREInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.RSHGRTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.RSHNEQInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.RSHSMEInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.obrn.RSHSMTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oexa.ASGADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oexa.ASGBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oexa.ASGBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oexa.ASGBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oexa.ASGDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oexa.ASGLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oexa.ASGMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oexa.ASGMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oexa.ASGRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oexa.ASGSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olan.LANADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olan.LANDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olan.LANMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olan.LANMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olan.LANSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olan.LORADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olan.LORDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olan.LORMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olan.LORMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olan.LORSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olbn.LANBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olbn.LANBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olbn.LANBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olbn.LANLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olbn.LANRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olbn.LORBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olbn.LORBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olbn.LORBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olbn.LORLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olbn.LORRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olln.LANLORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olln.LORLANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olrn.LANEQVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olrn.LANGREInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olrn.LANGRTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olrn.LANNEQInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olrn.LANSMEInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olrn.LANSMTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olrn.LOREQVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olrn.LORGREInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olrn.LORGRTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olrn.LORNEQInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olrn.LORSMEInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olrn.LORSMTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.EQVADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.EQVDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.EQVMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.EQVMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.EQVSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.GREADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.GREDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.GREMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.GREMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.GRESUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.GRTADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.GRTDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.GRTMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.GRTMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.GRTSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.NEQADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.NEQDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.NEQMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.NEQMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.NEQSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.SMEADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.SMEDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.SMEMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.SMEMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.SMESUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.SMTADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.SMTDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.SMTMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.SMTMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oran.SMTSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.EQVBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.EQVBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.EQVBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.EQVLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.EQVRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.GREBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.GREBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.GREBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.GRELSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.GRERSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.GRTBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.GRTBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.GRTBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.GRTLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.GRTRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.NEQBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.NEQBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.NEQBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.NEQLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.NEQRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.SMEBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.SMEBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.SMEBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.SMELSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.SMERSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.SMTBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.SMTBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.SMTBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.SMTLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orbn.SMTRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orln.EQVLANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orln.EQVLORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orln.GRELANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orln.GRELORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orln.GRTLANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orln.GRTLORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orln.NEQLANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orln.NEQLORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orln.SMELANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orln.SMELORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orln.SMTLANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orln.SMTLORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.EQVGREInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.EQVGRTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.EQVNEQInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.EQVSMEInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.EQVSMTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.GREEQVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.GREGRTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.GRENEQInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.GRESMEInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.GRESMTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.GRTEQVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.GRTGREInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.GRTNEQInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.GRTSMEInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.GRTSMTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.NEQEQVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.NEQGREInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.NEQGRTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.NEQSMEInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.NEQSMTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.SMEEQVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.SMEGREInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.SMEGRTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.SMENEQInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.SMESMTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.SMTEQVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.SMTGREInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.SMTGRTInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.SMTNEQInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.orrn.SMTSMEInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.stmt.SBCIInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.stmt.SBCRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.stmt.SGLRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.stmt.SRTRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.stmt.STDLInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.stmt.SWDRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.trap.BTRPInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.trap.CTRPInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.trap.ETRPInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.trap.STRPInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.trap.TTRPInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.trap.VTRPInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.vars.VBRPInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.vars.VCRPInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.vars.VRRPInfection;
import com.jcsa.jcmuta.project.Mutant;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lopt.models.dominate.CDominanceGraph;

/**
 * Collect all the state infection machine for each mutation operator.
 * 
 * @author yukimula
 *
 */
public class StateInfections {
	
	private static final Map<Object, StateInfection> infections = new HashMap<Object, StateInfection>();
	
	static {
		/** TRAP-CLASS **/
		infections.put(MutaClass.BTRP, new BTRPInfection());
		infections.put(MutaClass.CTRP, new CTRPInfection());
		infections.put(MutaClass.ETRP, new ETRPInfection());
		infections.put(MutaClass.STRP, new STRPInfection());
		infections.put(MutaClass.TTRP, new TTRPInfection());
		infections.put(MutaClass.VTRP, new VTRPInfection());
		
		/** STMT-CLASS **/
		infections.put(MutaClass.SBCR, new SBCRInfection());
		infections.put(MutaClass.SBCI, new SBCIInfection());
		infections.put(MutaClass.SWDR, new SWDRInfection());
		infections.put(MutaClass.SGLR, new SGLRInfection());
		infections.put(MutaClass.STDL, new STDLInfection());
		
		/** INCR-CLASS **/
		infections.put(MutaClass.UIOR, new UIORInfection());
		infections.put(MutaClass.UIOI, new UIOIInfection());
		infections.put(MutaClass.UIOD, new UIODInfection());
		infections.put(MutaClass.VINC, new VINCInfection());
		infections.put(MutaClass.UNOI, new UNOIInfection());
		infections.put(MutaClass.UNOD, new UNODInfection());
		
		/** VARS-CLASS **/
		infections.put(MutaClass.VBRP, new VBRPInfection());
		infections.put(MutaClass.VCRP, new VCRPInfection());
		infections.put(MutaClass.VRRP, new VRRPInfection());
		infections.put(MutaClass.SRTR, new SRTRInfection());
		
		/** OEXA **/
		infections.put(MutaOperator.assign_to_arith_add_assign, new ASGADDInfection());
		infections.put(MutaOperator.assign_to_arith_sub_assign, new ASGSUBInfection());
		infections.put(MutaOperator.assign_to_arith_mul_assign, new ASGMULInfection());
		infections.put(MutaOperator.assign_to_arith_div_assign, new ASGDIVInfection());
		infections.put(MutaOperator.assign_to_arith_mod_assign, new ASGMODInfection());
		infections.put(MutaOperator.assign_to_bitws_and_assign, new ASGBANInfection());
		infections.put(MutaOperator.assign_to_bitws_ior_assign, new ASGBORInfection());
		infections.put(MutaOperator.assign_to_bitws_xor_assign, new ASGBXRInfection());
		infections.put(MutaOperator.assign_to_bitws_lsh_assign, new ASGLSHInfection());
		infections.put(MutaOperator.assign_to_bitws_rsh_assign, new ASGRSHInfection());
		
		/** OAAN **/
		infections.put(MutaOperator.arith_add_to_arith_sub, new ADDSUBInfection());
		infections.put(MutaOperator.arith_add_to_arith_mul, new ADDMULInfection());
		infections.put(MutaOperator.arith_add_to_arith_div, new ADDDIVInfection());
		infections.put(MutaOperator.arith_add_to_arith_mod, new ADDMODInfection());
		infections.put(MutaOperator.arith_sub_to_arith_add, new SUBADDInfection());
		infections.put(MutaOperator.arith_sub_to_arith_mul, new SUBMULInfection());
		infections.put(MutaOperator.arith_sub_to_arith_div, new SUBDIVInfection());
		infections.put(MutaOperator.arith_sub_to_arith_mod, new SUBMODInfection());
		infections.put(MutaOperator.arith_mul_to_arith_add, new MULADDInfection());
		infections.put(MutaOperator.arith_mul_to_arith_sub, new MULSUBInfection());
		infections.put(MutaOperator.arith_mul_to_arith_div, new MULDIVInfection());
		infections.put(MutaOperator.arith_mul_to_arith_mod, new MULMODInfection());
		infections.put(MutaOperator.arith_div_to_arith_add, new DIVADDInfection());
		infections.put(MutaOperator.arith_div_to_arith_sub, new DIVSUBInfection());
		infections.put(MutaOperator.arith_div_to_arith_mul, new DIVMULInfection());
		infections.put(MutaOperator.arith_div_to_arith_mod, new DIVMODInfection());
		infections.put(MutaOperator.arith_mod_to_arith_add, new MODADDInfection());
		infections.put(MutaOperator.arith_mod_to_arith_sub, new MODSUBInfection());
		infections.put(MutaOperator.arith_mod_to_arith_mul, new MODMULInfection());
		infections.put(MutaOperator.arith_mod_to_arith_div, new MODDIVInfection());
		
		/** OABN **/
		infections.put(MutaOperator.arith_add_to_bitws_and, new ADDBANInfection());
		infections.put(MutaOperator.arith_add_to_bitws_ior, new ADDBORInfection());
		infections.put(MutaOperator.arith_add_to_bitws_xor, new ADDBXRInfection());
		infections.put(MutaOperator.arith_add_to_bitws_lsh, new ADDLSHInfection());
		infections.put(MutaOperator.arith_add_to_bitws_rsh, new ADDRSHInfection());
		infections.put(MutaOperator.arith_sub_to_bitws_and, new SUBBANInfection());
		infections.put(MutaOperator.arith_sub_to_bitws_ior, new SUBBORInfection());
		infections.put(MutaOperator.arith_sub_to_bitws_xor, new SUBBXRInfection());
		infections.put(MutaOperator.arith_sub_to_bitws_lsh, new SUBLSHInfection());
		infections.put(MutaOperator.arith_sub_to_bitws_rsh, new SUBRSHInfection());
		infections.put(MutaOperator.arith_mul_to_bitws_and, new MULBANInfection());
		infections.put(MutaOperator.arith_mul_to_bitws_ior, new MULBORInfection());
		infections.put(MutaOperator.arith_mul_to_bitws_xor, new MULBXRInfection());
		infections.put(MutaOperator.arith_mul_to_bitws_lsh, new MULLSHInfection());
		infections.put(MutaOperator.arith_mul_to_bitws_rsh, new MULRSHInfection());
		infections.put(MutaOperator.arith_div_to_bitws_and, new DIVBANInfection());
		infections.put(MutaOperator.arith_div_to_bitws_ior, new DIVBORInfection());
		infections.put(MutaOperator.arith_div_to_bitws_xor, new DIVBXRInfection());
		infections.put(MutaOperator.arith_div_to_bitws_lsh, new DIVLSHInfection());
		infections.put(MutaOperator.arith_div_to_bitws_rsh, new DIVRSHInfection());
		infections.put(MutaOperator.arith_mod_to_bitws_and, new MODBANInfection());
		infections.put(MutaOperator.arith_mod_to_bitws_ior, new MODBORInfection());
		infections.put(MutaOperator.arith_mod_to_bitws_xor, new MODBXRInfection());
		infections.put(MutaOperator.arith_mod_to_bitws_lsh, new MODLSHInfection());
		infections.put(MutaOperator.arith_mod_to_bitws_rsh, new MODRSHInfection());
		
		/** OALN **/
		infections.put(MutaOperator.arith_add_to_logic_and, new ADDLANInfection());
		infections.put(MutaOperator.arith_sub_to_logic_and, new SUBLANInfection());
		infections.put(MutaOperator.arith_mul_to_logic_and, new MULLANInfection());
		infections.put(MutaOperator.arith_div_to_logic_and, new DIVLANInfection());
		infections.put(MutaOperator.arith_mod_to_logic_and, new MODLANInfection());
		infections.put(MutaOperator.arith_add_to_logic_ior, new ADDLORInfection());
		infections.put(MutaOperator.arith_sub_to_logic_ior, new SUBLORInfection());
		infections.put(MutaOperator.arith_mul_to_logic_ior, new MULLORInfection());
		infections.put(MutaOperator.arith_div_to_logic_ior, new DIVLORInfection());
		infections.put(MutaOperator.arith_mod_to_logic_ior, new MODLORInfection());
		
		/** OARN **/
		infections.put(MutaOperator.arith_add_to_greater_tn, new ADDGRTInfection());
		infections.put(MutaOperator.arith_add_to_greater_eq, new ADDGREInfection());
		infections.put(MutaOperator.arith_add_to_smaller_tn, new ADDSMTInfection());
		infections.put(MutaOperator.arith_add_to_smaller_eq, new ADDSMEInfection());
		infections.put(MutaOperator.arith_add_to_equal_with, new ADDEQVInfection());
		infections.put(MutaOperator.arith_add_to_not_equals, new ADDNEQInfection());
		infections.put(MutaOperator.arith_sub_to_greater_tn, new SUBGRTInfection());
		infections.put(MutaOperator.arith_sub_to_greater_eq, new SUBGREInfection());
		infections.put(MutaOperator.arith_sub_to_smaller_tn, new SUBSMTInfection());
		infections.put(MutaOperator.arith_sub_to_smaller_eq, new SUBSMEInfection());
		infections.put(MutaOperator.arith_sub_to_equal_with, new SUBEQVInfection());
		infections.put(MutaOperator.arith_sub_to_not_equals, new SUBNEQInfection());
		infections.put(MutaOperator.arith_mul_to_greater_tn, new MULGRTInfection());
		infections.put(MutaOperator.arith_mul_to_greater_eq, new MULGREInfection());
		infections.put(MutaOperator.arith_mul_to_smaller_tn, new MULSMTInfection());
		infections.put(MutaOperator.arith_mul_to_smaller_eq, new MULSMEInfection());
		infections.put(MutaOperator.arith_mul_to_equal_with, new MULEQVInfection());
		infections.put(MutaOperator.arith_mul_to_not_equals, new MULNEQInfection());
		infections.put(MutaOperator.arith_div_to_greater_tn, new DIVGRTInfection());
		infections.put(MutaOperator.arith_div_to_greater_eq, new DIVGREInfection());
		infections.put(MutaOperator.arith_div_to_smaller_tn, new DIVSMTInfection());
		infections.put(MutaOperator.arith_div_to_smaller_eq, new DIVSMEInfection());
		infections.put(MutaOperator.arith_div_to_equal_with, new DIVEQVInfection());
		infections.put(MutaOperator.arith_div_to_not_equals, new DIVNEQInfection());
		infections.put(MutaOperator.arith_mod_to_greater_tn, new MODGRTInfection());
		infections.put(MutaOperator.arith_mod_to_greater_eq, new MODGREInfection());
		infections.put(MutaOperator.arith_mod_to_smaller_tn, new MODSMTInfection());
		infections.put(MutaOperator.arith_mod_to_smaller_eq, new MODSMEInfection());
		infections.put(MutaOperator.arith_mod_to_equal_with, new MODEQVInfection());
		infections.put(MutaOperator.arith_mod_to_not_equals, new MODNEQInfection());
		
		/** OBAN **/
		infections.put(MutaOperator.bitws_and_to_arith_add, new BANADDInfection());
		infections.put(MutaOperator.bitws_and_to_arith_sub, new BANSUBInfection());
		infections.put(MutaOperator.bitws_and_to_arith_mul, new BANMULInfection());
		infections.put(MutaOperator.bitws_and_to_arith_div, new BANDIVInfection());
		infections.put(MutaOperator.bitws_and_to_arith_mod, new BANMODInfection());
		infections.put(MutaOperator.bitws_ior_to_arith_add, new BORADDInfection());
		infections.put(MutaOperator.bitws_ior_to_arith_sub, new BORSUBInfection());
		infections.put(MutaOperator.bitws_ior_to_arith_mul, new BORMULInfection());
		infections.put(MutaOperator.bitws_ior_to_arith_div, new BORDIVInfection());
		infections.put(MutaOperator.bitws_ior_to_arith_mod, new BORMODInfection());
		infections.put(MutaOperator.bitws_xor_to_arith_add, new BXRADDInfection());
		infections.put(MutaOperator.bitws_xor_to_arith_sub, new BXRSUBInfection());
		infections.put(MutaOperator.bitws_xor_to_arith_mul, new BXRMULInfection());
		infections.put(MutaOperator.bitws_xor_to_arith_div, new BXRDIVInfection());
		infections.put(MutaOperator.bitws_xor_to_arith_mod, new BXRMODInfection());
		infections.put(MutaOperator.bitws_lsh_to_arith_add, new LSHADDInfection());
		infections.put(MutaOperator.bitws_lsh_to_arith_sub, new LSHSUBInfection());
		infections.put(MutaOperator.bitws_lsh_to_arith_mul, new LSHMULInfection());
		infections.put(MutaOperator.bitws_lsh_to_arith_div, new LSHDIVInfection());
		infections.put(MutaOperator.bitws_lsh_to_arith_mod, new LSHMODInfection());
		infections.put(MutaOperator.bitws_rsh_to_arith_add, new RSHADDInfection());
		infections.put(MutaOperator.bitws_rsh_to_arith_sub, new RSHSUBInfection());
		infections.put(MutaOperator.bitws_rsh_to_arith_mul, new RSHMULInfection());
		infections.put(MutaOperator.bitws_rsh_to_arith_div, new RSHDIVInfection());
		infections.put(MutaOperator.bitws_rsh_to_arith_mod, new RSHMODInfection());
		
		/** OBBN **/
		infections.put(MutaOperator.bitws_and_to_bitws_ior, new BANBORInfection());
		infections.put(MutaOperator.bitws_and_to_bitws_xor, new BANBXRInfection());
		infections.put(MutaOperator.bitws_and_to_bitws_lsh, new BANLSHInfection());
		infections.put(MutaOperator.bitws_and_to_bitws_rsh, new BANRSHInfection());
		infections.put(MutaOperator.bitws_ior_to_bitws_and, new BORBANInfection());
		infections.put(MutaOperator.bitws_ior_to_bitws_xor, new BORBXRInfection());
		infections.put(MutaOperator.bitws_ior_to_bitws_lsh, new BORLSHInfection());
		infections.put(MutaOperator.bitws_ior_to_bitws_rsh, new BORRSHInfection());
		infections.put(MutaOperator.bitws_xor_to_bitws_and, new BXRBANInfection());
		infections.put(MutaOperator.bitws_xor_to_bitws_ior, new BXRBORInfection());
		infections.put(MutaOperator.bitws_xor_to_bitws_lsh, new BXRLSHInfection());
		infections.put(MutaOperator.bitws_xor_to_bitws_rsh, new BXRRSHInfection());
		infections.put(MutaOperator.bitws_lsh_to_bitws_and, new LSHBANInfection());
		infections.put(MutaOperator.bitws_lsh_to_bitws_ior, new LSHBORInfection());
		infections.put(MutaOperator.bitws_lsh_to_bitws_xor, new LSHBXRInfection());
		infections.put(MutaOperator.bitws_lsh_to_bitws_rsh, new LSHRSHInfection());
		infections.put(MutaOperator.bitws_rsh_to_bitws_and, new RSHBANInfection());
		infections.put(MutaOperator.bitws_rsh_to_bitws_ior, new RSHBORInfection());
		infections.put(MutaOperator.bitws_rsh_to_bitws_xor, new RSHBXRInfection());
		infections.put(MutaOperator.bitws_rsh_to_bitws_lsh, new RSHLSHInfection());
		
		/** OBLN **/
		infections.put(MutaOperator.bitws_and_to_logic_and, new BANLANInfection());
		infections.put(MutaOperator.bitws_ior_to_logic_and, new BORLANInfection());
		infections.put(MutaOperator.bitws_xor_to_logic_and, new BXRLANInfection());
		infections.put(MutaOperator.bitws_lsh_to_logic_and, new LSHLANInfection());
		infections.put(MutaOperator.bitws_rsh_to_logic_and, new RSHLANInfection());
		infections.put(MutaOperator.bitws_and_to_logic_ior, new BANLORInfection());
		infections.put(MutaOperator.bitws_ior_to_logic_ior, new BORLORInfection());
		infections.put(MutaOperator.bitws_xor_to_logic_ior, new BXRLORInfection());
		infections.put(MutaOperator.bitws_lsh_to_logic_ior, new LSHLORInfection());
		infections.put(MutaOperator.bitws_rsh_to_logic_ior, new RSHLORInfection());
		
		/** OBRN **/
		infections.put(MutaOperator.bitws_and_to_greater_tn, new BANGRTInfection());
		infections.put(MutaOperator.bitws_and_to_greater_eq, new BANGREInfection());
		infections.put(MutaOperator.bitws_and_to_smaller_tn, new BANSMTInfection());
		infections.put(MutaOperator.bitws_and_to_smaller_eq, new BANSMEInfection());
		infections.put(MutaOperator.bitws_and_to_equal_with, new BANEQVInfection());
		infections.put(MutaOperator.bitws_and_to_not_equals, new BANNEQInfection());
		infections.put(MutaOperator.bitws_ior_to_greater_tn, new BORGRTInfection());
		infections.put(MutaOperator.bitws_ior_to_greater_eq, new BORGREInfection());
		infections.put(MutaOperator.bitws_ior_to_smaller_tn, new BORSMTInfection());
		infections.put(MutaOperator.bitws_ior_to_smaller_eq, new BORSMEInfection());
		infections.put(MutaOperator.bitws_ior_to_equal_with, new BOREQVInfection());
		infections.put(MutaOperator.bitws_ior_to_not_equals, new BORNEQInfection());
		infections.put(MutaOperator.bitws_xor_to_greater_tn, new BXRGRTInfection());
		infections.put(MutaOperator.bitws_xor_to_greater_eq, new BXRGREInfection());
		infections.put(MutaOperator.bitws_xor_to_smaller_tn, new BXRSMTInfection());
		infections.put(MutaOperator.bitws_xor_to_smaller_eq, new BXRSMEInfection());
		infections.put(MutaOperator.bitws_xor_to_equal_with, new BXREQVInfection());
		infections.put(MutaOperator.bitws_xor_to_not_equals, new BXRNEQInfection());
		infections.put(MutaOperator.bitws_lsh_to_greater_tn, new LSHGRTInfection());
		infections.put(MutaOperator.bitws_lsh_to_greater_eq, new LSHGREInfection());
		infections.put(MutaOperator.bitws_lsh_to_smaller_tn, new LSHSMTInfection());
		infections.put(MutaOperator.bitws_lsh_to_smaller_eq, new LSHSMEInfection());
		infections.put(MutaOperator.bitws_lsh_to_equal_with, new LSHEQVInfection());
		infections.put(MutaOperator.bitws_lsh_to_not_equals, new LSHNEQInfection());
		infections.put(MutaOperator.bitws_rsh_to_greater_tn, new RSHGRTInfection());
		infections.put(MutaOperator.bitws_rsh_to_greater_eq, new RSHGREInfection());
		infections.put(MutaOperator.bitws_rsh_to_smaller_tn, new RSHSMTInfection());
		infections.put(MutaOperator.bitws_rsh_to_smaller_eq, new RSHSMEInfection());
		infections.put(MutaOperator.bitws_rsh_to_equal_with, new RSHEQVInfection());
		infections.put(MutaOperator.bitws_rsh_to_not_equals, new RSHNEQInfection());
		
		/** OLAN **/
		infections.put(MutaOperator.logic_and_to_arith_add, new LANADDInfection());
		infections.put(MutaOperator.logic_and_to_arith_sub, new LANSUBInfection());
		infections.put(MutaOperator.logic_and_to_arith_mul, new LANMULInfection());
		infections.put(MutaOperator.logic_and_to_arith_div, new LANDIVInfection());
		infections.put(MutaOperator.logic_and_to_arith_mod, new LANMODInfection());
		infections.put(MutaOperator.logic_ior_to_arith_add, new LORADDInfection());
		infections.put(MutaOperator.logic_ior_to_arith_sub, new LORSUBInfection());
		infections.put(MutaOperator.logic_ior_to_arith_mul, new LORMULInfection());
		infections.put(MutaOperator.logic_ior_to_arith_div, new LORDIVInfection());
		infections.put(MutaOperator.logic_ior_to_arith_mod, new LORMODInfection());
		
		/** OLBN **/
		infections.put(MutaOperator.logic_and_to_bitws_and, new LANBANInfection());
		infections.put(MutaOperator.logic_and_to_bitws_ior, new LANBORInfection());
		infections.put(MutaOperator.logic_and_to_bitws_xor, new LANBXRInfection());
		infections.put(MutaOperator.logic_and_to_bitws_lsh, new LANLSHInfection());
		infections.put(MutaOperator.logic_and_to_bitws_rsh, new LANRSHInfection());
		infections.put(MutaOperator.logic_ior_to_bitws_and, new LORBANInfection());
		infections.put(MutaOperator.logic_ior_to_bitws_ior, new LORBORInfection());
		infections.put(MutaOperator.logic_ior_to_bitws_xor, new LORBXRInfection());
		infections.put(MutaOperator.logic_ior_to_bitws_lsh, new LORLSHInfection());
		infections.put(MutaOperator.logic_ior_to_bitws_rsh, new LORRSHInfection());
		
		/** OLLN **/
		infections.put(MutaOperator.logic_and_to_logic_ior, new LANLORInfection());
		infections.put(MutaOperator.logic_ior_to_logic_and, new LORLANInfection());
		
		/** OLRN **/
		infections.put(MutaOperator.logic_and_to_greater_tn, new LANGRTInfection());
		infections.put(MutaOperator.logic_and_to_greater_eq, new LANGREInfection());
		infections.put(MutaOperator.logic_and_to_smaller_tn, new LANSMTInfection());
		infections.put(MutaOperator.logic_and_to_smaller_eq, new LANSMEInfection());
		infections.put(MutaOperator.logic_and_to_equal_with, new LANEQVInfection());
		infections.put(MutaOperator.logic_and_to_not_equals, new LANNEQInfection());
		infections.put(MutaOperator.logic_ior_to_greater_tn, new LORGRTInfection());
		infections.put(MutaOperator.logic_ior_to_greater_eq, new LORGREInfection());
		infections.put(MutaOperator.logic_ior_to_smaller_tn, new LORSMTInfection());
		infections.put(MutaOperator.logic_ior_to_smaller_eq, new LORSMEInfection());
		infections.put(MutaOperator.logic_ior_to_equal_with, new LOREQVInfection());
		infections.put(MutaOperator.logic_ior_to_not_equals, new LORNEQInfection());
		
		/** ORAN **/
		infections.put(MutaOperator.greater_tn_to_arith_add, new GRTADDInfection());
		infections.put(MutaOperator.greater_tn_to_arith_sub, new GRTSUBInfection());
		infections.put(MutaOperator.greater_tn_to_arith_mul, new GRTMULInfection());
		infections.put(MutaOperator.greater_tn_to_arith_div, new GRTDIVInfection());
		infections.put(MutaOperator.greater_tn_to_arith_mod, new GRTMODInfection());
		infections.put(MutaOperator.greater_eq_to_arith_add, new GREADDInfection());
		infections.put(MutaOperator.greater_eq_to_arith_sub, new GRESUBInfection());
		infections.put(MutaOperator.greater_eq_to_arith_mul, new GREMULInfection());
		infections.put(MutaOperator.greater_eq_to_arith_div, new GREDIVInfection());
		infections.put(MutaOperator.greater_eq_to_arith_mod, new GREMODInfection());
		infections.put(MutaOperator.smaller_tn_to_arith_add, new SMTADDInfection());
		infections.put(MutaOperator.smaller_tn_to_arith_sub, new SMTSUBInfection());
		infections.put(MutaOperator.smaller_tn_to_arith_mul, new SMTMULInfection());
		infections.put(MutaOperator.smaller_tn_to_arith_div, new SMTDIVInfection());
		infections.put(MutaOperator.smaller_tn_to_arith_mod, new SMTMODInfection());
		infections.put(MutaOperator.smaller_eq_to_arith_add, new SMEADDInfection());
		infections.put(MutaOperator.smaller_eq_to_arith_sub, new SMESUBInfection());
		infections.put(MutaOperator.smaller_eq_to_arith_mul, new SMEMULInfection());
		infections.put(MutaOperator.smaller_eq_to_arith_div, new SMEDIVInfection());
		infections.put(MutaOperator.smaller_eq_to_arith_mod, new SMEMODInfection());
		infections.put(MutaOperator.equal_with_to_arith_add, new EQVADDInfection());
		infections.put(MutaOperator.equal_with_to_arith_sub, new EQVSUBInfection());
		infections.put(MutaOperator.equal_with_to_arith_mul, new EQVMULInfection());
		infections.put(MutaOperator.equal_with_to_arith_div, new EQVDIVInfection());
		infections.put(MutaOperator.equal_with_to_arith_mod, new EQVMODInfection());
		infections.put(MutaOperator.not_equals_to_arith_add, new NEQADDInfection());
		infections.put(MutaOperator.not_equals_to_arith_sub, new NEQSUBInfection());
		infections.put(MutaOperator.not_equals_to_arith_mul, new NEQMULInfection());
		infections.put(MutaOperator.not_equals_to_arith_div, new NEQDIVInfection());
		infections.put(MutaOperator.not_equals_to_arith_mod, new NEQMODInfection());
		
		/** ORBN **/
		infections.put(MutaOperator.greater_tn_to_bitws_and, new GRTBANInfection());
		infections.put(MutaOperator.greater_tn_to_bitws_ior, new GRTBORInfection());
		infections.put(MutaOperator.greater_tn_to_bitws_xor, new GRTBXRInfection());
		infections.put(MutaOperator.greater_tn_to_bitws_lsh, new GRTLSHInfection());
		infections.put(MutaOperator.greater_tn_to_bitws_rsh, new GRTRSHInfection());
		infections.put(MutaOperator.greater_eq_to_bitws_and, new GREBANInfection());
		infections.put(MutaOperator.greater_eq_to_bitws_ior, new GREBORInfection());
		infections.put(MutaOperator.greater_eq_to_bitws_xor, new GREBXRInfection());
		infections.put(MutaOperator.greater_eq_to_bitws_lsh, new GRELSHInfection());
		infections.put(MutaOperator.greater_eq_to_bitws_rsh, new GRERSHInfection());
		infections.put(MutaOperator.smaller_tn_to_bitws_and, new SMTBANInfection());
		infections.put(MutaOperator.smaller_tn_to_bitws_ior, new SMTBORInfection());
		infections.put(MutaOperator.smaller_tn_to_bitws_xor, new SMTBXRInfection());
		infections.put(MutaOperator.smaller_tn_to_bitws_lsh, new SMTLSHInfection());
		infections.put(MutaOperator.smaller_tn_to_bitws_rsh, new SMTRSHInfection());
		infections.put(MutaOperator.smaller_eq_to_bitws_and, new SMEBANInfection());
		infections.put(MutaOperator.smaller_eq_to_bitws_ior, new SMEBORInfection());
		infections.put(MutaOperator.smaller_eq_to_bitws_xor, new SMEBXRInfection());
		infections.put(MutaOperator.smaller_eq_to_bitws_lsh, new SMELSHInfection());
		infections.put(MutaOperator.smaller_eq_to_bitws_rsh, new SMERSHInfection());
		infections.put(MutaOperator.equal_with_to_bitws_and, new EQVBANInfection());
		infections.put(MutaOperator.equal_with_to_bitws_ior, new EQVBORInfection());
		infections.put(MutaOperator.equal_with_to_bitws_xor, new EQVBXRInfection());
		infections.put(MutaOperator.equal_with_to_bitws_lsh, new EQVLSHInfection());
		infections.put(MutaOperator.equal_with_to_bitws_rsh, new EQVRSHInfection());
		infections.put(MutaOperator.not_equals_to_bitws_and, new NEQBANInfection());
		infections.put(MutaOperator.not_equals_to_bitws_ior, new NEQBORInfection());
		infections.put(MutaOperator.not_equals_to_bitws_xor, new NEQBXRInfection());
		infections.put(MutaOperator.not_equals_to_bitws_lsh, new NEQLSHInfection());
		infections.put(MutaOperator.not_equals_to_bitws_rsh, new NEQRSHInfection());
		
		/** ORLN **/
		infections.put(MutaOperator.greater_tn_to_logic_and, new GRTLANInfection());
		infections.put(MutaOperator.greater_tn_to_logic_ior, new GRTLORInfection());
		infections.put(MutaOperator.greater_eq_to_logic_and, new GRELANInfection());
		infections.put(MutaOperator.greater_eq_to_logic_ior, new GRELORInfection());
		infections.put(MutaOperator.smaller_tn_to_logic_and, new SMTLANInfection());
		infections.put(MutaOperator.smaller_tn_to_logic_ior, new SMTLORInfection());
		infections.put(MutaOperator.smaller_eq_to_logic_and, new SMELANInfection());
		infections.put(MutaOperator.smaller_eq_to_logic_ior, new SMELORInfection());
		infections.put(MutaOperator.equal_with_to_logic_and, new EQVLANInfection());
		infections.put(MutaOperator.equal_with_to_logic_ior, new EQVLORInfection());
		infections.put(MutaOperator.not_equals_to_logic_and, new NEQLANInfection());
		infections.put(MutaOperator.not_equals_to_logic_ior, new NEQLORInfection());
		
		/** ORRN **/
		infections.put(MutaOperator.greater_tn_to_greater_eq, new GRTGREInfection());
		infections.put(MutaOperator.greater_tn_to_smaller_tn, new GRTSMTInfection());
		infections.put(MutaOperator.greater_tn_to_smaller_eq, new GRTSMEInfection());
		infections.put(MutaOperator.greater_tn_to_equal_with, new GRTEQVInfection());
		infections.put(MutaOperator.greater_tn_to_not_equals, new GRTNEQInfection());
		infections.put(MutaOperator.greater_eq_to_greater_tn, new GREGRTInfection());
		infections.put(MutaOperator.greater_eq_to_smaller_tn, new GRESMTInfection());
		infections.put(MutaOperator.greater_eq_to_smaller_eq, new GRESMEInfection());
		infections.put(MutaOperator.greater_eq_to_equal_with, new GREEQVInfection());
		infections.put(MutaOperator.greater_eq_to_not_equals, new GRENEQInfection());
		infections.put(MutaOperator.smaller_tn_to_greater_tn, new SMTGRTInfection());
		infections.put(MutaOperator.smaller_tn_to_greater_eq, new SMTGREInfection());
		infections.put(MutaOperator.smaller_tn_to_smaller_eq, new SMTSMEInfection());
		infections.put(MutaOperator.smaller_tn_to_equal_with, new SMTEQVInfection());
		infections.put(MutaOperator.smaller_tn_to_not_equals, new SMTNEQInfection());
		infections.put(MutaOperator.smaller_eq_to_greater_tn, new SMEGRTInfection());
		infections.put(MutaOperator.smaller_eq_to_greater_eq, new SMEGREInfection());
		infections.put(MutaOperator.smaller_eq_to_smaller_tn, new SMESMTInfection());
		infections.put(MutaOperator.smaller_eq_to_equal_with, new SMEEQVInfection());
		infections.put(MutaOperator.smaller_eq_to_not_equals, new SMENEQInfection());
		infections.put(MutaOperator.equal_with_to_greater_tn, new EQVGRTInfection());
		infections.put(MutaOperator.equal_with_to_greater_eq, new EQVGREInfection());
		infections.put(MutaOperator.equal_with_to_smaller_tn, new EQVSMTInfection());
		infections.put(MutaOperator.equal_with_to_smaller_eq, new EQVSMEInfection());
		infections.put(MutaOperator.equal_with_to_not_equals, new EQVNEQInfection());
		infections.put(MutaOperator.not_equals_to_greater_tn, new NEQGRTInfection());
		infections.put(MutaOperator.not_equals_to_greater_eq, new NEQGREInfection());
		infections.put(MutaOperator.not_equals_to_smaller_tn, new NEQSMTInfection());
		infections.put(MutaOperator.not_equals_to_smaller_eq, new NEQSMEInfection());
		infections.put(MutaOperator.not_equals_to_equal_with, new NEQEQVInfection());
		
		/** TODO complement the other mutation operators here **/
	}
	
	/**
	 * open the optimization on symbolic constraints
	 */
	protected static void open_optimize_constraint() {
		for(StateInfection infection : infections.values()) {
			infection.open_optimize_constraint();
		}
	}
	
	/**
	 * close the optimization on symbolic constraints
	 */
	protected static void close_optimize_constraint() {
		for(StateInfection infection : infections.values()) {
			infection.close_optimize_constraint();
		}
	}
	
	/**
	 * whether to optimize the constraints being described
	 * @param optimize
	 */
	public static void set_optimize(boolean optimize) {
		if(optimize)
			open_optimize_constraint();
		else 
			close_optimize_constraint();
	}
	
	/**
	 * parse the syntactic mutation to its semantic description based on state error graph
	 * @param cir_tree
	 * @param mutation
	 * @param dgraph
	 * @return
	 * @throws Exception
	 */
	protected static StateErrorGraph parse(CirTree cir_tree, AstMutation mutation, CDominanceGraph dgraph) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("C-intermediate tree is not provided");
		else if(mutation == null)
			throw new IllegalArgumentException("Invalid syntactic mutation as null");
		else if(dgraph == null)
			throw new IllegalArgumentException("Invalid dominance graph is provided");
		else if(infections.containsKey(mutation.get_mutation_class())) {
			return infections.get(mutation.get_mutation_class()).parse(cir_tree, mutation, dgraph);
		}
		else if(infections.containsKey(mutation.get_mutation_operator())) {
			return infections.get(mutation.get_mutation_operator()).parse(cir_tree, mutation, dgraph);
		}
		else throw new IllegalArgumentException("Not support for: " + mutation);
	}
	
	/**
	 * generate the infection module for the source code mutation
	 * @param cir_tree
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	public static MutantInfection infect(CirTree cir_tree, Mutant mutant) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("C-intermediate tree is not provided");
		else if(mutant == null)
			throw new IllegalArgumentException("Invalid syntactic mutation as null");
		else {
			AstMutation mutation = mutant.get_mutation();
			if(infections.containsKey(mutation.get_mutation_class())) {
				return infections.get(mutation.get_mutation_class()).infect(cir_tree, mutant);
			}
			else if(infections.containsKey(mutation.get_mutation_operator())) {
				return infections.get(mutation.get_mutation_operator()).infect(cir_tree, mutant);
			}
			else throw new IllegalArgumentException("Not support for: " + mutation);
		}
	}
	
}
