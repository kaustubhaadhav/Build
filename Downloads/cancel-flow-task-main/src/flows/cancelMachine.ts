import type { AB } from '@/types/cancellation';

export type Step =
  | 'found_or_not'
  | 'congrats'
  | 'feedback_found'
  | 'visa_followup'
  | 'end_found'
  | 'downsell'
  | 'feedback_not_found'
  | 'reason'
  | 'reason_followup'
  | 'end_not_found';

export interface Answers {
  found_job?: boolean;
  found_via_platform?: boolean;
  visa?: string | null;
  reason?: string | null;
  willing_to_pay?: number | null;
}

export function buildSteps(ab: AB, a: Answers): Step[] {
  const steps: Step[] = ['found_or_not'];

  if (a.found_job === true) {
    steps.push('congrats', 'feedback_found');
    if (a.found_via_platform === true) steps.push('visa_followup');
    steps.push('end_found');
    return steps;
  }

  if (a.found_job === false) {
    if (ab === 'A') steps.push('downsell'); // B skips downsell
    steps.push('feedback_not_found', 'reason');
    if (a.reason === 'too_expensive') steps.push('reason_followup');
    steps.push('end_not_found');
  }

  return steps;
}

export const nextStep = (s: Step[], c: Step) => s[Math.min(s.indexOf(c)+1, s.length-1)] ?? c;
export const prevStep = (s: Step[], c: Step) => s[Math.max(s.indexOf(c)-1, 0)] ?? c;
