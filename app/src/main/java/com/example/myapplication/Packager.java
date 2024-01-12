package com.example.myapplication;

import org.jpos.iso.IFA_LLCHAR;
import org.jpos.iso.IFA_NUMERIC;
import org.jpos.iso.IFB_AMOUNT;
import org.jpos.iso.IFB_BINARY;
import org.jpos.iso.IFB_BITMAP;
import org.jpos.iso.IFB_LLBINARY;
import org.jpos.iso.IFB_LLCHAR;
import org.jpos.iso.IFB_LLLBINARY;
import org.jpos.iso.IFB_LLLCHAR;
import org.jpos.iso.IFB_LLLNUM;
import org.jpos.iso.IFB_LLNUM;
import org.jpos.iso.IFB_NUMERIC;
import org.jpos.iso.IF_CHAR;
import org.jpos.iso.ISOBasePackager;
import org.jpos.iso.ISOComponent;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOFieldPackager;
import org.jpos.iso.ISOUtil;

import java.util.BitSet;
public class Packager extends ISOBasePackager{
    /**C_IFB_BITMAP class that handles the extraction of data
     *  from a bitmap field in ISO 8583 messages,
     *  taking into account the presence of specific bits in the bitmap to determine its length**/
    public static class C_IFB_BITMAP extends IFB_BITMAP {

        public C_IFB_BITMAP(int len, String description) {
            super(len, description);
        }

        /**
         * We have to override the default because there's a miscalculation of the
         * length of the bitmap
         * */

        @Override
        public int unpack(ISOComponent c, byte[] b, int offset) throws ISOException {
            int len;

            BitSet bmap = ISOUtil.byte2BitSet (b, offset, getLength());
            c.setValue(bmap);
            len = bmap.get(1) ? 128 : 64;

            if (getLength() > 64 && bmap.get(65))
                len = 192;


            return Math.min (getLength(), len >> 3);
        }
    }


    protected ISOFieldPackager fld[] = {
            /*000*/ new IFB_NUMERIC(4, "Message Type Indicator", true),
            /*001*/ new C_IFB_BITMAP(64, "Bitmap"),
            /*002*/ new IFB_LLNUM(20, "Primary Account number",true),
            /*003*/ new IFB_NUMERIC(6, "Processing Code", true),
            /*004*/ new IFB_NUMERIC(12, "Amount, Transaction", true),
            /*005*/ new IFB_NUMERIC(12, "Amount, Reconciliation", true),
            /*006*/ new IFB_NUMERIC(12, "Amount, Cardholder billing", true),
            /*007*/ new IFB_NUMERIC(10, "Date and time, transmission", true),
            /*008*/ new IFB_NUMERIC(8, "Amount, Cardholder billing fee", true),
            /*009*/ new IFB_NUMERIC(8, "Conversion rate, Reconciliation", true),
            /*010*/ new IFB_NUMERIC(8, "Conversion rate, Cardholder billing", true),
            /*011*/ new IFB_NUMERIC(6, "Systems trace audit number", true),
            /*012*/ new IFB_NUMERIC(6, "Date and time, Local transaction", true),
            /*013*/ new IFB_NUMERIC(4, "Date, Effective", true),
            /*014*/ new IFB_NUMERIC(4, "Date, Expiration", true),
            /*015*/ new IFB_NUMERIC(4, "Date, Settlement", true),
            /*016*/ new IFB_NUMERIC(4, "Date, Conversion", true),
            /*017*/ new IFB_NUMERIC(4, "Date, Capture", true),
            /*018*/ new IFB_NUMERIC(4, "Merchant type", true),
            /*019*/ new IFB_NUMERIC(3, "Country code, Acquiring institution", true),
            /*020*/ new IFB_NUMERIC(3, "Country code, Primary account number", true),
            /*021*/ new IFB_NUMERIC(3, "Country code, Forwarding institution", true),
            /*022*/ new IFB_NUMERIC(3, "POS Entry mode", true),
            /*023*/ new IFB_NUMERIC(2, "Card sequence number", true),
            /*024*/ new IFB_NUMERIC(3, "Function code", true),
            /*025*/ new IFB_NUMERIC(2, "POS Condition code", true),
            /*026*/ new IFB_NUMERIC(4, "Card acceptor business code", true),
            /*027*/ new IFB_NUMERIC(1, "Approval code length", true),
            /*028*/ new IFB_NUMERIC(6, "Date, Reconciliation", true),
            /*029*/ new IFB_NUMERIC(3, "Reconciliation indicator", true),
            /*030*/ new IFB_NUMERIC(24, "Amounts, original", true),
            /*031*/ new IFB_NUMERIC(9, "Acquirer reference data", true),
            /*032*/ new IFB_LLNUM(11, "Acquirer institution ident code", true),
            /*033*/ new IFB_LLNUM(11, "Forwarding institution ident code", true),
            /*034*/ new IFB_LLNUM(99, "Primary account number, extended", true),
            /*035*/ new IFB_LLNUM(38, "Track 2 data", true),// Up to 37
            /*036*/ new IFB_LLLNUM(104, "Track 3 data", true),
            /*037*/ new IF_CHAR(12, "Retrieval reference number"),
            /*038*/ new IF_CHAR(6, "Approval code"),
            /*039*/ new IF_CHAR(2, "Action code"),
            /*040*/ new IF_CHAR(3, "Service code"),
            /*041*/ new IFA_NUMERIC(8, "Card acceptor terminal identification"),
            /*042*/ new IFA_NUMERIC(15, "Card acceptor identification code"),
            /*043*/ new IFB_LLCHAR(99, "Card acceptor name/location"),
            /*044*/ new IF_CHAR(10, "Additional response data"),
            /*045*/ new IFA_LLCHAR(76, "Track 1 data"),
            /*046*/ new IFB_LLLCHAR(999, "Amounts, Fees"),
            /*047*/ new IFB_LLLCHAR(999, "Additional data - national"),
            /*048*/ new IFB_LLLCHAR(999, "Additional data - private"), // up to 120
            /*049*/ new IF_CHAR(3, "Currency code, Transaction"),
            /*050*/ new IF_CHAR(3, "Currency code, Reconciliation"),
            /*051*/ new IF_CHAR(3, "Currency code, Cardholder billing"),
            /*052*/ new IFB_BINARY(8, "Personal identification number (PIN) data"), // IFB_NUMERIC 16
            /*053*/ new IFB_LLCHAR(16, "Security related control information"),
            /*054*/ new IFB_LLLCHAR(999, "Amounts, additional"),
            /*055*/ new IFB_LLLBINARY(999, "IC card system related data"),
            /*056*/ new IFB_LLNUM(35, "Original data elements", true),
            /*057*/ new IFB_NUMERIC(3, "Authorization life cycle code", true),
            /*058*/ new IFB_LLNUM(11, "Authorizing agent institution Id Code", true),
            /*059*/ new IFB_LLLCHAR(999, "Transport data"),
            /*060*/ new IFB_LLLCHAR(999, "Reserved for national use"),
            /*061*/ new IFB_LLLCHAR(999, "Reserved for national use"),
            /*062*/ new IFB_LLLCHAR(999, "Reserved for private use"),
            /*063*/ new IFB_LLLCHAR(999, "Reserved for private use"),
            /*064*/ new IFB_LLBINARY(64, "Message authentication code field"),
            /*065*/ new IFB_BINARY(8, "Reserved for ISO use"),
            /*066*/ new IFB_LLLCHAR(204, "Amounts, original fees"),
            /*067*/ new IFB_NUMERIC(2, "Extended payment data", true),
            /*068*/ new IFB_NUMERIC(3, "Country code, receiving institution", true),
            /*069*/ new IFB_NUMERIC(3, "Country code, settlement institution", true),
            /*070*/ new IFB_NUMERIC(3, "Country code, authorizing agent Inst.", true),
            /*071*/ new IFB_NUMERIC(8, "Message number", true),
            /*072*/ new IFB_LLLCHAR(999, "Data record"),
            /*073*/ new IFB_NUMERIC(6, "Date, action", true),
            /*074*/ new IFB_NUMERIC(10, "Credits, number", true),
            /*075*/ new IFB_NUMERIC(10, "Credits, reversal number", true),
            /*076*/ new IFB_NUMERIC(10, "Debits, number", true),
            /*077*/ new IFB_NUMERIC(10, "Debits, reversal number", true),
            /*078*/ new IFB_NUMERIC(10, "Transfer, number", true),
            /*079*/ new IFB_NUMERIC(10, "Transfer, reversal number", true),
            /*080*/ new IFB_NUMERIC(10, "Inquiries, number", true),
            /*081*/ new IFB_NUMERIC(10, "Authorizations, number", true),
            /*082*/ new IFB_NUMERIC(10, "Inquiries, reversal number", true),
            /*083*/ new IFB_NUMERIC(10, "Payments, number", true),
            /*084*/ new IFB_NUMERIC(10, "Payments, reversal number", true),
            /*085*/ new IFB_NUMERIC(10, "Fee collections, number", true),
            /*086*/ new IFB_NUMERIC(16, "Credits, amount", true),
            /*087*/ new IFB_NUMERIC(16, "Credits, reversal amount", true),
            /*088*/ new IFB_NUMERIC(16, "Debits, amount", true),
            /*089*/ new IFB_NUMERIC(16, "Debits, reversal amount", true),
            /*090*/ new IFB_NUMERIC(10, "Authorizations, reversal number", true),
            /*091*/ new IFB_NUMERIC(3, "Country code, transaction Dest. Inst.", true),
            /*092*/ new IFB_NUMERIC(3, "Country code, transaction Orig. Inst.", true),
            /*093*/ new IFB_LLNUM(11, "Transaction Dest. Inst. Id code", true),
            /*094*/ new IFB_LLNUM(11, "Transaction Orig. Inst. Id code", true),
            /*095*/ new IFB_LLCHAR(99, "Card issuer reference data"),
            /*096*/ new IFB_LLLBINARY(999, "Key management data"),
            /*097*/ new IFB_AMOUNT(1 + 16, "Amount, Net reconciliation", true),
            /*098*/ new IF_CHAR(25, "Payee"),
            /*099*/ new IFB_LLCHAR(11, "Settlement institution Id code"),
            /*100*/ new IFB_LLNUM(11, "Receiving institution Id code", true),
            /*101*/ new IFB_LLCHAR(17, "File name"),
            /*102*/ new IFB_LLCHAR(28, "Account identification 1"),
            /*103*/ new IFB_LLCHAR(28, "Account identification 2"),
            /*104*/ new IFB_LLLCHAR(255, "Transaction description"),
            /*105*/ new IFB_NUMERIC(16, "Credits, Chargeback amount", true),
            /*106*/ new IFB_NUMERIC(16, "Debits, Chargeback amount", true),
            /*107*/ new IFB_NUMERIC(10, "Credits, Chargeback number", true),
            /*108*/ new IFB_NUMERIC(10, "Debits, Chargeback number", true),
            /*109*/ new IFB_LLCHAR(84, "Credits, Fee amounts"),
            /*110*/ new IFB_LLCHAR(84, "Debits, Fee amounts"),
            /*111*/ new IFB_LLLCHAR(999, "Reserved for ISO use"),
            /*112*/ new IFB_LLLCHAR(999, "Reserved for ISO use"),
            /*113*/ new IFB_LLLCHAR(999, "Reserved for ISO use"),
            /*114*/ new IFB_LLLCHAR(999, "Reserved for ISO use"),
            /*115*/ new IFB_LLLCHAR(999, "Reserved for ISO use"),
            /*116*/ new IFB_LLLCHAR(999, "Reserved for national use"),
            /*117*/ new IFB_LLLCHAR(999, "Reserved for national use"),
            /*118*/ new IFB_LLLCHAR(999, "Reserved for national use"),
            /*119*/ new IFB_LLLCHAR(999, "Reserved for national use"),
            /*120*/ new IFB_LLLCHAR(999, "Reserved for national use"),
            /*121*/ new IFB_LLLCHAR(999, "Reserved for national use"),
            /*122*/ new IFB_LLLCHAR(999, "Reserved for national use"),
            /*123*/ new IFB_LLLCHAR(999, "Reserved for private use"),
            /*124*/ new IFB_LLLCHAR(999, "Reserved for private use"),
            /*125*/ new IFB_LLLCHAR(999, "Reserved for private use"),
            /*126*/ new IFB_LLLCHAR(999, "Reserved for private use"),
            /*127*/ new IFB_LLLCHAR(999, "Reserved for private use"),
            /*128*/ new IFB_BINARY(8, "Message authentication code field")
    };

    public Packager() {
        super();
        setFieldPackager(fld);
    }

}