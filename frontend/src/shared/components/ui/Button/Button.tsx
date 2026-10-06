import type { ButtonHTMLAttributes } from 'react';

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement>;

/**
 * 共通のボタン。type の既定値は button にする。
 */
export function Button({ type = 'button', ...props }: ButtonProps) {
  return <button type={type} {...props} />;
}
