export type VisaType = 'H1B'|'H4'|'L1'|'TN'|'E3'|'Other';
export type CancelReason = 'too_expensive'|'not_finding_jobs'|'got_offer_elsewhere'|'low_quality'|'not_using'|'other';
export type AB = 'A'|'B';

export interface Cancellation {
  id: string;
  found_job: boolean | null;
  found_via_platform: boolean | null;
  visa: VisaType | null;
  reason: CancelReason | null;
  willing_to_pay: number | null;
  feedback: string | null;
  ab_variant: AB | null;
  started_at: string;
  completed_at: string | null;
}
