/**
 */
package org.bangbangbang.cvl.provider;


import java.util.Collection;
import java.util.List;

import org.bangbangbang.cvl.CvlFactory;
import org.bangbangbang.cvl.CvlPackage;
import org.bangbangbang.cvl.Operation;
import org.bangbangbang.cvl.OperationCallExp;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a {@link org.bangbangbang.cvl.OperationCallExp} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class OperationCallExpItemProvider
	extends BCLExpressionItemProvider
	implements
		IEditingDomainItemProvider,
		IStructuredItemContentProvider,
		ITreeItemContentProvider,
		IItemLabelProvider,
		IItemPropertySource {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public OperationCallExpItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			addOperationPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Operation feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addOperationPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_OperationCallExp_operation_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_OperationCallExp_operation_feature", "_UI_OperationCallExp_type"),
				 CvlPackage.Literals.OPERATION_CALL_EXP__OPERATION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			super.getChildrenFeatures(object);
			childrenFeatures.add(CvlPackage.Literals.OPERATION_CALL_EXP__ARGUMENT);
		}
		return childrenFeatures;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child) {
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildFeature(object, child);
	}

	/**
	 * This returns OperationCallExp.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/OperationCallExp"));
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		Operation labelValue = ((OperationCallExp)object).getOperation();
		String label = labelValue == null ? null : labelValue.toString();
		return label == null || label.length() == 0 ?
			getString("_UI_OperationCallExp_type") :
			getString("_UI_OperationCallExp_type") + " " + label;
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(OperationCallExp.class)) {
			case CvlPackage.OPERATION_CALL_EXP__OPERATION:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
				return;
			case CvlPackage.OPERATION_CALL_EXP__ARGUMENT:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
				return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);

		newChildDescriptors.add
			(createChildParameter
				(CvlPackage.Literals.OPERATION_CALL_EXP__ARGUMENT,
				 CvlFactory.eINSTANCE.createBCLExpression()));

		newChildDescriptors.add
			(createChildParameter
				(CvlPackage.Literals.OPERATION_CALL_EXP__ARGUMENT,
				 CvlFactory.eINSTANCE.createVSpecRef()));

		newChildDescriptors.add
			(createChildParameter
				(CvlPackage.Literals.OPERATION_CALL_EXP__ARGUMENT,
				 CvlFactory.eINSTANCE.createOperationCallExp()));

		newChildDescriptors.add
			(createChildParameter
				(CvlPackage.Literals.OPERATION_CALL_EXP__ARGUMENT,
				 CvlFactory.eINSTANCE.createBooleanLiteralExp()));

		newChildDescriptors.add
			(createChildParameter
				(CvlPackage.Literals.OPERATION_CALL_EXP__ARGUMENT,
				 CvlFactory.eINSTANCE.createStringLiteralExp()));

		newChildDescriptors.add
			(createChildParameter
				(CvlPackage.Literals.OPERATION_CALL_EXP__ARGUMENT,
				 CvlFactory.eINSTANCE.createNumericLiteralExp()));

		newChildDescriptors.add
			(createChildParameter
				(CvlPackage.Literals.OPERATION_CALL_EXP__ARGUMENT,
				 CvlFactory.eINSTANCE.createIntegerLiteralExp()));

		newChildDescriptors.add
			(createChildParameter
				(CvlPackage.Literals.OPERATION_CALL_EXP__ARGUMENT,
				 CvlFactory.eINSTANCE.createUnlimitedLiteralExp()));

		newChildDescriptors.add
			(createChildParameter
				(CvlPackage.Literals.OPERATION_CALL_EXP__ARGUMENT,
				 CvlFactory.eINSTANCE.createRealLiteralExp()));
	}

}
