package com.alextim.SFI.transfer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MFlag {
    MSP_M_fRECEIVE(1),
    MSP_M_fTRANSMIT(2),
    MSP_M_fMODECODE(4),
    MSP_M_fBROADCAST(8),
    // тип сообщения для "расширенного" сообщения
    MSP_M_EXTENDED(0x10),
    // тип сообщения для терминальной команды
    MSP_M_EXTENDED_TERM(0x30),

    //Допустимые значения в поле type
    MSP_M_BC_TO_RT(MSP_M_fRECEIVE.value),
    MSP_M_RT_TO_BC(MSP_M_fTRANSMIT.value),
    MSP_M_RT_TO_RT(MSP_M_fRECEIVE.value | MSP_M_fTRANSMIT.value),
    MSP_M_BC_TO_RT_BROADCAST(MSP_M_fRECEIVE.value | MSP_M_fBROADCAST.value),
    MSP_M_RTtoRT_BROADCAST(MSP_M_fRECEIVE.value | MSP_M_fTRANSMIT.value | MSP_M_fBROADCAST.value),
    MSP_M_MODECODE(MSP_M_fMODECODE.value),
    MSP_M_MODECODE_DATA_TX(MSP_M_fMODECODE.value | MSP_M_fTRANSMIT.value),

    MSP_M_MODECODE_DATA_RX(MSP_M_fMODECODE.value | MSP_M_fRECEIVE.value),

    MSP_M_MODECODE_BROADCAST(MSP_M_fMODECODE.value | MSP_M_fBROADCAST.value),

    MSP_M_MODECODE_DATA_BROADCAST(MSP_M_fMODECODE.value | MSP_M_fRECEIVE.value | MSP_M_fBROADCAST.value),
    //И недопустимые
    MSP_M_UNDEFINED(0);

    public final int value;
}
