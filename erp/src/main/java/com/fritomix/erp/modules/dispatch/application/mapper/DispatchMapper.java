package com.fritomix.erp.modules.dispatch.application.mapper;

import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.dispatch.application.dto.response.DispatchResponse;
import com.fritomix.erp.modules.dispatch.application.dto.response.DispatchResponse.ArrumeResponse;
import com.fritomix.erp.modules.dispatch.application.dto.response.DispatchResponse.DispatchDetailResponse;
import com.fritomix.erp.modules.dispatch.application.dto.response.DispatchResponse.OrderInfo;
import com.fritomix.erp.modules.dispatch.domain.entity.Arrume;
import com.fritomix.erp.modules.dispatch.domain.entity.Dispatch;
import com.fritomix.erp.modules.dispatch.domain.entity.DispatchDetail;
import com.fritomix.erp.modules.drivers.domain.entity.Driver;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.orders.domain.entity.OrderDetail;
import com.fritomix.erp.modules.vehicles.domain.entity.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DispatchMapper {

    private final UserRepository userRepository;

    public DispatchResponse toResponse(Dispatch dispatch) {
        List<DispatchDetail> details = dispatch.getDetails();
        if (details == null) details = Collections.emptyList();

        List<Order> orders = dispatch.getOrders();
        if (orders == null) orders = Collections.emptyList();

        List<OrderInfo> orderInfos = orders.stream()
                .map(o -> OrderInfo.builder()
                        .id(o.getId())
                        .orderNumber(o.getOrderNumber())
                        .clientName(o.getCustomer() != null ? o.getCustomer().getBusinessName() : null)
                        .pesoTotalCargue(o.getPesoTotalCargue())
                        .numeroFactura(dispatch.getFacturasPorPedido() != null ? dispatch.getFacturasPorPedido().get(o.getId()) : null)
                        .build())
                .collect(Collectors.toList());

        Order firstOrder = orders.isEmpty() ? null : orders.get(0);
        BigDecimal pesoTotal = orders.stream()
                .flatMap(o -> o.getDetails() == null ? Collections.<OrderDetail>emptyList().stream() : o.getDetails().stream())
                .map(d -> {
                    BigDecimal unitWeight = d.getProduct() != null && d.getProduct().getPesoUnidad() != null
                            ? d.getProduct().getPesoUnidad()
                            : BigDecimal.ZERO;
                    return unitWeight.multiply(d.getQuantity() != null ? d.getQuantity() : BigDecimal.ZERO);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalDimension = orders.stream()
                .flatMap(o -> o.getDetails() == null ? Collections.<OrderDetail>emptyList().stream() : o.getDetails().stream())
                .map(d -> {
                    BigDecimal dim = d.getProduct() != null && d.getProduct().getDimension() != null
                            ? d.getProduct().getDimension()
                            : BigDecimal.ZERO;
                    return dim.multiply(d.getQuantity() != null ? d.getQuantity() : BigDecimal.ZERO);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        String dispatchUserName = null;
        if (dispatch.getUserId() != null) {
            dispatchUserName = userRepository.findById(dispatch.getUserId())
                    .map(u -> u.getFirstName() + " " + u.getLastName())
                    .orElse(null);
        }

        Driver driver = dispatch.getDriver();
        Vehicle vehicle = dispatch.getVehicle();

        DispatchResponse.DispatchResponseBuilder builder = DispatchResponse.builder()
                .id(dispatch.getId())
                .dispatchNumber(dispatch.getDispatchNumber())
                .tipoPedido(dispatch.getTipoPedido())
                .orders(orderInfos)
                .pesoTotal(pesoTotal)
                .totalDimension(totalDimension)
                .pesoTotalCargue(pesoTotal)
                .dispatchDate(dispatch.getDispatchDate())
                .status(dispatch.getStatus())
                .cumplimiento(dispatch.getCumplimiento())
                .notes(dispatch.getNotes())
                .numeroFactura(dispatch.getNumeroFactura())
                .dispatchUserName(dispatchUserName)
                .details(details.stream().map(this::toDetailResponse).collect(Collectors.toList()))
                .arrumes(dispatch.getArrumes() == null
                        ? Collections.emptyList()
                        : dispatch.getArrumes().stream().map(this::toArrumeResponse).collect(Collectors.toList()))
                .createdAt(dispatch.getCreatedAt());


        if (firstOrder != null) {
            builder.orderId(firstOrder.getId())
                   .orderNumber(firstOrder.getOrderNumber());
        }
        if (driver != null) {
            builder.driverId(driver.getId())
                   .driverName(driver.getName())
                   .driverDocument(driver.getDocument());
        }
        if (vehicle != null) {
            builder.vehicleId(vehicle.getId())
                   .vehicleNumber(vehicle.getVehicleNumber())
                   .vehicleType(vehicle.getType());
        }

        return builder.build();
    }

    private DispatchDetailResponse toDetailResponse(DispatchDetail detail) {
        DispatchDetailResponse.DispatchDetailResponseBuilder builder = DispatchDetailResponse.builder()
                .id(detail.getId())
                .quantity(detail.getQuantity())
                .delivered(detail.getDelivered())
                .observations(detail.getObservations())
                .detalleProducto(detail.getDetalleProducto())
                .lote(detail.getLote());

        if (detail.getProduct() != null) {
            builder.productId(detail.getProduct().getId())
                   .productName(detail.getProduct().getName())
                   .productCode(detail.getProduct().getCode());
        }

        return builder.build();
    }

    private ArrumeResponse toArrumeResponse(Arrume arrume) {
        return ArrumeResponse.builder()
                .id(arrume.getId())
                .numArrume(arrume.getNumArrume())
                .arrumeProducto(arrume.getArrumeProducto())
                .cantidad(arrume.getCantidad())
                .lote(arrume.getLote())
                .build();
    }
}
