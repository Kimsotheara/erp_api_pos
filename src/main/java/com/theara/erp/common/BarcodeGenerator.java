package com.theara.erp.common;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.util.EnumMap;
import java.util.Map;

/**
 * Renders barcode values into printable images. Uses Code128 (alphanumeric, scanner-friendly).
 */
public final class BarcodeGenerator {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 200;

    private BarcodeGenerator() {
    }

    /**
     * Builds a numeric internal barcode value derived from the product id, zero-padded to 12 digits.
     * Deterministic and unique because the id is unique.
     */
    public static String internalValue(Long id) {
        return String.format("%012d", id);
    }

    /** Renders the given value as a Code128 PNG image. */
    public static byte[] toPng(String value) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Product has no barcode to render");
        }
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 10);
            BitMatrix matrix = new MultiFormatWriter()
                    .encode(value, BarcodeFormat.CODE_128, WIDTH, HEIGHT, hints);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to render barcode: " + e.getMessage());
        }
    }
}
