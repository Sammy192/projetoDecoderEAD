package com.ead.payment.services.impl;

import com.ead.payment.configs.exceptions.BusinessException;
import com.ead.payment.configs.exceptions.NotFoundException;
import com.ead.payment.dto.PaymentCommandDTO;
import com.ead.payment.dto.PaymentRequestDTO;
import com.ead.payment.enums.PaymentStatusControlEnum;
import com.ead.payment.models.CreditCardModel;
import com.ead.payment.models.PaymentModel;
import com.ead.payment.models.UserModel;
import com.ead.payment.publishers.PaymentCommandPublisher;
import com.ead.payment.repositories.CreditCardRepository;
import com.ead.payment.repositories.PaymentRepository;
import com.ead.payment.repositories.UserRepository;
import com.ead.payment.services.PaymentService;
import com.ead.payment.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private Logger logger = LogManager.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final CreditCardRepository creditCardRepository;
    private final UserService userService;
    private final PaymentCommandPublisher paymentCommandPublisher;


    public PaymentServiceImpl(PaymentRepository paymentRepository, UserRepository userRepository, CreditCardRepository creditCardRepository, UserService userService, PaymentCommandPublisher paymentCommandPublisher) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.creditCardRepository = creditCardRepository;
        this.userService = userService;
        this.paymentCommandPublisher = paymentCommandPublisher;
    }

    @Override
    public PaymentModel requestPayment(PaymentRequestDTO paymentRequest, UUID userId) {
        UserModel userModel = userService.findById(userId);
        validaPagamento(userModel);
        CreditCardModel creditCardModel = salvaCartaoCredito(paymentRequest, userModel);
        PaymentModel paymentModel = createPayment(paymentRequest, userModel);

        publishPaymentCommand(userModel, paymentModel, creditCardModel);

        return paymentModel;
    }

    private void publishPaymentCommand(UserModel userModel, PaymentModel paymentModel, CreditCardModel creditCardModel) {
        try {
            PaymentCommandDTO paymentCommandDTO = new PaymentCommandDTO(userModel.getUserId(), paymentModel.getPaymentId(), creditCardModel.getCardId());
            paymentCommandPublisher.publishPaymentCommand(paymentCommandDTO);
        } catch (Exception e) {
            logger.error("Error sending payment command message: Error {}", e.getMessage());
        }
    }

    private PaymentModel createPayment(PaymentRequestDTO paymentRequest, UserModel userModel) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setPaymentControl(PaymentStatusControlEnum.PENDING);
        paymentModel.setPaymentRequestDate(LocalDateTime.now(ZoneId.of("UTC")));
        paymentModel.setPaymentExpirationDate(LocalDateTime.now(ZoneId.of("UTC")).plusMonths(12));
        paymentModel.setLastDigitsCreditCard(paymentRequest.creditCardNumber().substring(paymentRequest.creditCardNumber().length()-4));
        paymentModel.setValuePaid(paymentRequest.valuePaid());
        paymentModel.setUser(userModel);
        paymentRepository.save(paymentModel);
        return paymentModel;
    }

    private CreditCardModel salvaCartaoCredito(PaymentRequestDTO paymentRequest, UserModel userModel) {
        CreditCardModel creditCardModel = creditCardRepository
                                            .findByUser(userModel)
                                            .orElseGet(CreditCardModel::new);

        BeanUtils.copyProperties(paymentRequest, creditCardModel);
        creditCardModel.setUser(userModel);
        creditCardRepository.save(creditCardModel);
        return creditCardModel;
    }

    private void validaPagamento(UserModel userModel) {
        PaymentModel lastPaymentByUser = findLastPaymentByUser(userModel);

        if (Objects.nonNull(lastPaymentByUser)) {
            if (PaymentStatusControlEnum.PENDING.equals(lastPaymentByUser.getPaymentControl())) {
                throw new BusinessException("Pagamento pendente ou em processamento.");
            }
            if (PaymentStatusControlEnum.APPROVED.equals(lastPaymentByUser.getPaymentControl())
                    && lastPaymentByUser.getPaymentExpirationDate().isAfter(LocalDateTime.now(ZoneId.of("UTC")))) {
                throw new BusinessException("Pagamento já realizado.");
            }
        }
    }

    private PaymentModel findLastPaymentByUser(UserModel userModel) {
        return paymentRepository.findTopByUserOrderByPaymentRequestDateDesc(userModel).orElse(null);
    }

    @Override
    public Page<PaymentModel> findAllByUser(Specification<PaymentModel> spec, Pageable pageable) {
        return paymentRepository.findAll(spec, pageable);
    }

    @Override
    public PaymentModel findPaymentByUser(UUID userId, UUID paymentId) {
        Optional<PaymentModel> paymentModelOptional = paymentRepository.findByUserUserIdAndPaymentId(userId, paymentId);
        return paymentModelOptional.orElseThrow(() -> new NotFoundException("Error: Payment not found for this user."));
    }
}
